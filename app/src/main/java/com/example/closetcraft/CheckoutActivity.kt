package com.example.closetcraft

import android.animation.ObjectAnimator
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class CheckoutActivity : AppCompatActivity() {

    private lateinit var btnPayNow: Button
    private lateinit var tvTotal: TextView
    private lateinit var etCardNumber: EditText
    private lateinit var etExpiry: EditText
    private lateinit var etCvv: EditText
    private lateinit var etNameOnCard: EditText
    private lateinit var ivVisa: ImageView
    private lateinit var ivMastercard: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        // Views
        btnPayNow = findViewById(R.id.btnPayNow)
        tvTotal = findViewById(R.id.tv_total_checkout)
        etCardNumber = findViewById(R.id.etCardNumber)
        etExpiry = findViewById(R.id.etExpiry)
        etCvv = findViewById(R.id.etCvv)
        etNameOnCard = findViewById(R.id.etNameOnCard)
        ivVisa = findViewById(R.id.ivVisa)
        ivMastercard = findViewById(R.id.ivMastercard)

        btnPayNow.isEnabled = false
        updatePayButtonStyle()

        val total = intent.getDoubleExtra("EXTRA_TOTAL", 0.0)
        tvTotal.text = "Total: R %.2f".format(total)

        // Card number auto-format #### #### #### ####
        etCardNumber.addTextChangedListener(object : TextWatcher {
            private var isFormatting = false
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) return
                s ?: return

                val digits = s.toString().replace(" ", "")
                val formatted = digits.chunked(4).joinToString(" ")
                isFormatting = true
                s.replace(0, s.length, formatted)
                isFormatting = false

                validateInputs()
            }
        })

        // Expiry auto-format MM/YY (robust)
        etExpiry.addTextChangedListener(object : TextWatcher {
            private var isFormatting = false
            private var previousText = ""

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                previousText = s.toString()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) return
                s ?: return

                isFormatting = true

                val digits = s.toString().replace("/", "")
                val formatted = StringBuilder()
                for (i in digits.indices) {
                    if (i == 2) formatted.append("/")
                    formatted.append(digits[i])
                }

                // Get cursor from EditText, not Editable
                val cursorPosition = etExpiry.selectionStart
                s.replace(0, s.length, formatted.toString())
                // Move cursor safely
                etExpiry.setSelection(cursorPosition.coerceAtMost(formatted.length))

                isFormatting = false
                validateInputs()
            }
        })


        // Shared watcher for CVV and Name
        val fieldWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { validateInputs() }
        }

        etCvv.addTextChangedListener(fieldWatcher)
        etNameOnCard.addTextChangedListener(fieldWatcher)

        btnPayNow.setOnClickListener {
            showPaymentConfirmation(total)
        }
    }

    private fun validateInputs() {
        val cardNum = etCardNumber.text.toString().replace(" ", "")
        val expiry = etExpiry.text.toString().trim()
        val cvv = etCvv.text.toString().trim()
        val name = etNameOnCard.text.toString().trim()

        // Validate card number
        val isCardValid = cardNum.length == 16 && cardNum.all { it.isDigit() }

        // Animate card icons
        when {
            cardNum.startsWith("4") -> { // Visa
                fadeIcon(ivVisa, 1f)
                fadeIcon(ivMastercard, 0.3f)
            }
            cardNum.startsWith("5") -> { // Mastercard
                fadeIcon(ivMastercard, 1f)
                fadeIcon(ivVisa, 0.3f)
            }
            else -> {
                fadeIcon(ivVisa, 0.3f)
                fadeIcon(ivMastercard, 0.3f)
            }
        }

        // Validate expiry MM/YY
        val isExpiryValid = expiry.matches(Regex("(0[1-9]|1[0-2])/\\d{2}")) &&
                isFutureExpiry(expiry)

        // Validate CVV
        val isCvvValid = cvv.length == 3 && cvv.all { it.isDigit() }

        // Name not empty
        val isNameValid = name.isNotEmpty()

        // Enable pay button only if all valid
        btnPayNow.isEnabled = isCardValid && isExpiryValid && isCvvValid && isNameValid
        updatePayButtonStyle()
    }

    private fun fadeIcon(imageView: ImageView, targetAlpha: Float) {
        ObjectAnimator.ofFloat(imageView, "alpha", imageView.alpha, targetAlpha)
            .setDuration(200)
            .start()
    }

    private fun isFutureExpiry(expiry: String): Boolean {
        val parts = expiry.split("/")
        if (parts.size != 2) return false
        val month = parts[0].toIntOrNull() ?: return false
        val year = 2000 + (parts[1].toIntOrNull() ?: return false)
        val now = Calendar.getInstance()
        val currentYear = now.get(Calendar.YEAR)
        val currentMonth = now.get(Calendar.MONTH) + 1
        return year > currentYear || (year == currentYear && month >= currentMonth)
    }

    private fun updatePayButtonStyle() {
        if (btnPayNow.isEnabled) {
            btnPayNow.setBackgroundResource(R.drawable.paynow_button)
            btnPayNow.setTextColor(resources.getColor(android.R.color.holo_green_dark))
        } else {
            btnPayNow.setBackgroundResource(R.drawable.paynow_button_disabled)
            btnPayNow.setTextColor(resources.getColor(android.R.color.darker_gray))
        }
    }


    private fun showPaymentConfirmation(total: Double) {
        AlertDialog.Builder(this)
            .setTitle("Confirm Payment")
            .setMessage("Do you want to pay R %.2f?".format(total))
            .setPositiveButton("Yes") { _, _ ->
                AlertDialog.Builder(this)
                    .setTitle("Payment Successful")
                    .setMessage("Your payment of R %.2f was successful!".format(total))
                    .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                    .show()
            }
            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}
