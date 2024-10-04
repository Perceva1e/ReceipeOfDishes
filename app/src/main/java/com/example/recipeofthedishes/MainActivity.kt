package com.example.recipeofthedishes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Locale

class MainActivity : BaseActivity() {
    init {
        System.loadLibrary("user_validation")
    }

    external fun validateUser(userName: String, userEmail: String, userPassword: String): String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val userName: EditText = findViewById(R.id.UserLogin)
        val userPassword: EditText = findViewById(R.id.UserPassword)
        val buttonSingUp: Button = findViewById(R.id.buttonReg)
        val userEmail: EditText = findViewById(R.id.EmailUser)
        val linkToSingIn: TextView = findViewById(R.id.linkToSingIn)
        val buttonChangeLanguage: Button = findViewById(R.id.buttonChangeLanguage)

        linkToSingIn.setOnClickListener {
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
        }

        buttonSingUp.setOnClickListener {
            val checkUserName = userName.text.toString().trim()
            val checkPassword = userPassword.text.toString().trim()
            val checkUserEmail = userEmail.text.toString().trim()
            val validationMessage = validateUser(checkUserName, checkUserEmail, checkPassword)
            Toast.makeText(this, validationMessage, Toast.LENGTH_LONG).show()
            if (validationMessage == "User added and signed in") {
                val user = User(checkUserName, checkUserEmail, checkPassword)
                val db = DbHelper(this, null)
                db.addUser(user)
                val userId = db.getUserId(checkUserName, checkPassword)
                saveUserId(userId)
                val intent = Intent(this, ItemsActivity::class.java)
                startActivity(intent)
                userName.text.clear()
                userPassword.text.clear()
                userEmail.text.clear()
            }
        }


        buttonChangeLanguage.setOnClickListener {
            val currentLanguage = Locale.getDefault().language
            val newLanguage = when (currentLanguage) {
                "en" -> "ru"
                "ru" -> "be"
                else -> "en"
            }
            setLocale(newLanguage, this)
            recreate()
        }
    }

    private fun saveUserId(userId: Int) {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("user_id", userId)
        editor.apply()
    }
}
