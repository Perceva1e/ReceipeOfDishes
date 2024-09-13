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

class AuthActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_auth)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val linkToSignUp: TextView = findViewById(R.id.linkToSingUp)
        val userName: EditText = findViewById(R.id.UserLoginAuth)
        val userPassword: EditText = findViewById(R.id.UserPasswordAuth)
        val buttonSignIn: Button = findViewById(R.id.buttonAuth)
        val buttonChangeLanguage: Button = findViewById(R.id.buttonChangeLanguageAuth)

        linkToSignUp.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        buttonSignIn.setOnClickListener {
            val checkUserName = userName.text.toString().trim()
            val checkPassword = userPassword.text.toString().trim()

            if (checkUserName.isEmpty() || checkPassword.isEmpty()) {
                Toast.makeText(this, "Not all fields are filled in", Toast.LENGTH_LONG).show()
            } else {
                val db = DbHelper(this, null)
                val isAuth = db.getUser(checkUserName, checkPassword)
                if (isAuth) {
                    val userId = db.getUserId(checkUserName, checkPassword)
                    saveUserId(userId)

                    Toast.makeText(this, "User $checkUserName is signed in", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, ItemsActivity::class.java)
                    startActivity(intent)
                    userName.text.clear()
                    userPassword.text.clear()
                } else {
                    Toast.makeText(this, "User $checkUserName isn't signed in", Toast.LENGTH_LONG).show()
                }
            }
        }

        buttonChangeLanguage.setOnClickListener {
            val newLanguage = if (Locale.getDefault().language == "en") "ru" else "en"
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
