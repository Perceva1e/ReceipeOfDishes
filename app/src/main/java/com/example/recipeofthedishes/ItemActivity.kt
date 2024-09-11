package com.example.recipeofthedishes

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ItemActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_item)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val title: TextView = findViewById(R.id.itemListTitleOne)
        val text:  TextView = findViewById(R.id.itemListTextOne)
        val instruction: TextView = findViewById(R.id.itemListInstruction)
        val backwardButton: Button = findViewById(R.id.itemListButtonOne)
        title.text = intent.getStringExtra("itemTitle")
        text.text = intent.getStringExtra("itemText")
        instruction.text = intent.getStringExtra("itemInstruction")
        backwardButton.setOnClickListener{
            val intent = Intent(this, ItemsActivity::class.java)
            startActivity(intent)
        }
    }
}