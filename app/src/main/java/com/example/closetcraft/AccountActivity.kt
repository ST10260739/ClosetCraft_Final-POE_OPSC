package com.example.closetcraft

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale

class AccountActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var themeSpinner: Spinner
    private lateinit var settingsText: TextView
    private lateinit var languageToggle: TextView

    private val PREFS_NAME = "app_settings"
    private val KEY_LANGUAGE = "app_language"

    override fun onCreate(savedInstanceState: Bundle?) {

        // ✅ Load saved language BEFORE UI is drawn
        loadLocale()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        auth = FirebaseAuth.getInstance()

        settingsText = findViewById(R.id.settingsText)
        themeSpinner = findViewById(R.id.themeSpinner)
        languageToggle = findViewById(R.id.languageToggle)

        // ✅ Set toggle text based on stored language
        val savedLang = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            .getString(KEY_LANGUAGE, "en")
        languageToggle.text = if (savedLang == "af") "AF" else "EN"

        // --- Theme Switch Dropdown ---
        settingsText.setOnClickListener {
            themeSpinner.visibility = if (themeSpinner.visibility == View.GONE) View.VISIBLE else View.GONE
        }

        themeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // ✅ LANGUAGE TOGGLE BUTTON FUNCTION
        languageToggle.setOnClickListener {
            if (languageToggle.text == "EN") {
                setLocale("af")
                languageToggle.text = "AF"
            } else {
                setLocale("en")
                languageToggle.text = "EN"
            }
        }

        // --- Sign Out ---
        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            auth.signOut()
            navigateToLogin()
        }

        // --- Navigation ---
        findViewById<LinearLayout>(R.id.nav_home).setOnClickListener {
            startActivity(Intent(this, WomenActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.nav_cart).setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.nav_favourites).setOnClickListener {
            startActivity(Intent(this, WishlistActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.nav_account).setOnClickListener {
            startActivity(Intent(this, AccountActivity::class.java))
        }
        findViewById<TextView>(R.id.account).setOnClickListener {
            startActivity(Intent(this, MyAccountActivity::class.java))
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    // ✅ Save & Apply Language, then Refresh UI
    private fun setLocale(languageCode: String) {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        prefs.edit().putString(KEY_LANGUAGE, languageCode).apply()

        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        recreate()
    }

    // ✅ Load Language Before Display
    private fun loadLocale() {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val language = prefs.getString(KEY_LANGUAGE, "en") ?: "en"

        val locale = Locale(language)
        Locale.setDefault(locale)

        val config = resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}
