package com.example.closetcraft

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // ---------------- Redirect if already logged in ----------------
        if (auth.currentUser != null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        val backBtn = findViewById<ImageView>(R.id.backBtn)
        backBtn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        val etFname = findViewById<EditText>(R.id.etFname)
        val etLname = findViewById<EditText>(R.id.etLname)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etContact = findViewById<EditText>(R.id.etContact)
        val btnSignup = findViewById<Button>(R.id.btnSignup)

        // ---------------- Email/Password Sign-Up ----------------
        btnSignup.setOnClickListener {
            val firstName = etFname.text.toString().trim()
            val lastName = etLname.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val contact = etContact.text.toString().trim()

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || contact.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Toast.makeText(
                            this,
                            "Sign-Up Failed: ${task.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                        return@addOnCompleteListener
                    }

                    val user = auth.currentUser
                    if (user == null) {
                        Toast.makeText(this, "User creation failed", Toast.LENGTH_LONG).show()
                        return@addOnCompleteListener
                    }

                    val uid = user.uid
                    val userMap = hashMapOf(
                        "firstName" to firstName,
                        "lastName" to lastName,
                        "email" to email,
                        "contact" to contact
                    )

                    firestore.collection("users").document(uid)
                        .set(userMap)
                        .addOnCompleteListener { setTask ->
                            if (setTask.isSuccessful) {
                                Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show()
                            } else {
                                Log.e("Signup", "Failed to save user info: ${setTask.exception}")
                                Toast.makeText(this, "Registration partially complete", Toast.LENGTH_SHORT).show()
                            }

                            // ✅ Redirect to LoginActivity after signup
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        }
                }
        }
    }
}
