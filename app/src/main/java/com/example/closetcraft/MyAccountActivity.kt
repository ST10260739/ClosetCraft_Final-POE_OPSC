package com.example.closetcraft

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.MotionEvent
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MyAccountActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var btnDeleteAccount: Button

    private lateinit var tvFullName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvFname: TextView
    private lateinit var tvLastname: TextView
    private lateinit var tvContact: TextView
    private lateinit var tvPassword: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_account_details)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        tvFullName = findViewById(R.id.signUp) // Account Info
        tvEmail = findViewById(R.id.email)
        tvFname = findViewById(R.id.Fname)
        tvLastname = findViewById(R.id.Lastname)
        tvContact = findViewById(R.id.Contact)
        tvPassword = findViewById(R.id.password)

        btnDeleteAccount = findViewById(R.id.btnDeleteAccount)
        btnDeleteAccount.setOnClickListener { showDeleteConfirmationDialog() }

        findViewById<ImageView>(R.id.back).setOnClickListener {
            startActivity(Intent(this, AccountActivity::class.java))
            finish()
        }

        // Load user data
        loadUserDetails()

        // Set editable fields with small pencil icons
        setEditableOnTouch(tvFname, "firstName")
        setEditableOnTouch(tvLastname, "lastName")
        setEditableOnTouch(tvEmail, "email")
        setEditableOnTouch(tvContact, "contact")
    }

    private fun loadUserDetails() {
        val user = auth.currentUser ?: return
        val uid = user.uid

        firestore.collection("users").document(uid)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val firstName = doc.getString("firstName") ?: ""
                    val lastName = doc.getString("lastName") ?: ""
                    val email = doc.getString("email") ?: user.email ?: ""
                    val contact = doc.getString("contact") ?: ""

                    tvFname.text = firstName
                    tvLastname.text = lastName
                    tvFullName.text = "$firstName $lastName"
                    tvEmail.text = email
                    tvContact.text = contact
                    tvPassword.text = "********" // masked
                } else {
                    Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    // Extension function to convert dp to px
    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()

    // Resize drawable to dp size
    private fun Drawable.resizeDrawable(dpWidth: Int, dpHeight: Int): Drawable {
        val bitmap = (this as BitmapDrawable).bitmap
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, dpWidth.dpToPx(), dpHeight.dpToPx(), true)
        return BitmapDrawable(resources, scaledBitmap)
    }

    private fun setEditableOnTouch(textView: TextView, field: String) {
        val editDrawable = ContextCompat.getDrawable(this, R.drawable.ic_edit)?.resizeDrawable(10, 10)
        textView.compoundDrawablePadding = 8
        textView.gravity = android.view.Gravity.CENTER_VERTICAL

        textView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                // Show small pencil icon only while editing
                textView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, editDrawable, null)

                val editText = EditText(this)
                editText.setText(textView.text)
                val dialog = AlertDialog.Builder(this)
                    .setTitle("Update ${field.replaceFirstChar { it.uppercase() }}")
                    .setView(editText)
                    .setPositiveButton("Update") { _, _ ->
                        val newValue = editText.text.toString().trim()
                        if (newValue.isEmpty()) {
                            Toast.makeText(this, "$field cannot be empty", Toast.LENGTH_SHORT).show()
                            return@setPositiveButton
                        }

                        val uid = auth.currentUser?.uid ?: return@setPositiveButton
                        firestore.collection("users").document(uid)
                            .update(field, newValue)
                            .addOnSuccessListener {
                                textView.text = newValue
                                if (field == "firstName" || field == "lastName") {
                                    tvFullName.text = "${tvFname.text} ${tvLastname.text}"
                                }
                                Toast.makeText(this, "$field updated!", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Failed to update: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                    }
                    .setNegativeButton("Cancel", null)
                    .create()

                // Remove pencil after editing
                dialog.setOnDismissListener {
                    textView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
                }
                dialog.show()

                true
            } else {
                false
            }
        }
    }

    private fun showDeleteConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Account")
        builder.setMessage("Are you sure you want to delete your account? This action cannot be undone.")
        builder.setPositiveButton("Delete") { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
            deleteAccount()
        }
        builder.setNegativeButton("Cancel") { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun deleteAccount() {
        val user = auth.currentUser ?: return
        val uid = user.uid

        firestore.collection("users").document(uid)
            .delete()
            .addOnSuccessListener {
                user.delete().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Account deleted successfully", Toast.LENGTH_SHORT).show()
                        auth.signOut()
                        navigateToLogin()
                    } else {
                        Toast.makeText(this, "Failed to delete account: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, SignupActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
