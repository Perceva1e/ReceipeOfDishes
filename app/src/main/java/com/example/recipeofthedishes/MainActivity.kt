package com.example.recipeofthedishes

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
            if (checkUserName == "" || checkPassword == "" || checkUserEmail == "") {
                Toast.makeText(this, "Not all fields are filled in", Toast.LENGTH_LONG).show()
            } else {
                val user = User(checkUserName, checkUserEmail, checkPassword)
                val db = DbHelper(this, null)
                db.addUser(user)
                Toast.makeText(this, "User $checkUserName is added ", Toast.LENGTH_LONG).show()
                userName.text.clear()
                userPassword.text.clear()
                userEmail.text.clear()
            }
        }

        buttonChangeLanguage.setOnClickListener {
            val newLanguage = if (Locale.getDefault().language == "en") "ru" else "en"
            setLocale(newLanguage, this)
            recreate()
        }
    }
}
