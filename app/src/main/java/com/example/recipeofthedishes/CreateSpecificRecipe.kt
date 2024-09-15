package com.example.recipeofthedishes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class CreateSpecificRecipe : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_specific_recipe)
        val editTextImage: EditText = findViewById(R.id.editTextImage)
        val editTextTitle: EditText = findViewById(R.id.editTextTitle)
        val editTextIngredient: EditText = findViewById(R.id.editTextIngredient)
        val editTextDesc: EditText = findViewById(R.id.editTextDesc)
        val editTextInstructions: EditText = findViewById(R.id.editTextInstructions)
        val buttonSaveItem: Button = findViewById(R.id.buttonSaveItem)
        val buttonBack: Button = findViewById(R.id.buttonBack)
        val videoEditText : EditText = findViewById(R.id.editTextVideo)
        buttonBack.setOnClickListener {
            val intent = Intent(this@CreateSpecificRecipe, ItemsActivity::class.java)
            startActivity(intent)
            finish()
        }
        buttonSaveItem.setOnClickListener {
            val image = editTextImage.text.toString()
            val title = editTextTitle.text.toString()
            val ingredient = editTextIngredient.text.toString()
            val desc = editTextDesc.text.toString()
            val instructions = editTextInstructions.text.toString()
            val video = videoEditText.text.toString()
            if (title.isEmpty() || desc.isEmpty() || image.isEmpty() || instructions.isEmpty() || ingredient.isEmpty() || video.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_LONG).show()
            } else {
                val dbHelper = DbHelper(this@CreateSpecificRecipe, null)
                val userId = getCurrentUserId()
                val newItem = Item(0, image, title, ingredient, desc, instructions,video)
                dbHelper.addItem(userId, newItem)

                Toast.makeText(
                    this@CreateSpecificRecipe,
                    "Recipe added successfully",
                    Toast.LENGTH_LONG
                ).show()
                val intent = Intent(this@CreateSpecificRecipe, ItemsActivity::class.java)
                startActivity(intent)
                finish()
            }

        }
    }
    private fun getCurrentUserId(): Int {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("user_id", -1)
    }
}
