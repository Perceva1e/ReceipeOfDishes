package com.example.recipeofthedishes

import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class listIngredient : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_list_ingridient)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val ingredient: TextView = findViewById(R.id.Ingredient)
        val video: TextView = findViewById(R.id.videoUrl)
        val buttonBackToListRecipes: Button = findViewById(R.id.buttonBackToList)

        ingredient.text = intent.getStringExtra("itemIngredient")
        video.text = intent.getStringExtra("video")
        video.movementMethod = LinkMovementMethod.getInstance()

        buttonBackToListRecipes.setOnClickListener {
            val intent = Intent(this, ItemsActivity::class.java)
            startActivity(intent)
        }
    }
}
