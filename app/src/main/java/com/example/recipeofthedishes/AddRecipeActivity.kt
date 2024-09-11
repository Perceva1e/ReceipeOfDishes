package com.example.recipeofthedishes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import networks.RecipeRepository

class AddRecipeActivity : AppCompatActivity() {

    private val recipeRepository = RecipeRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_recipe)

        val editTextTitle: EditText = findViewById(R.id.editTextTitle)
        val editText: EditText = findViewById(R.id.editText)
        val buttonAddRecipe: Button = findViewById(R.id.buttonAddRecipe)
        val buttonBack: Button = findViewById(R.id.buttonBack)

        buttonBack.setOnClickListener{
            val intent = Intent(this@AddRecipeActivity, ItemsActivity::class.java)
            startActivity(intent)
            finish()
        }

        buttonAddRecipe.setOnClickListener {
            val title = editTextTitle.text.toString().trim()
            val description = editText.text.toString().trim()

            if (title.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_LONG).show()
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    val meal = recipeRepository.loadMealImageByName(title)
                    val imageUrl = meal?.strMealThumb ?: ""

                    if (imageUrl.isEmpty()) {
                        Toast.makeText(this@AddRecipeActivity, "Image not found for the recipe", Toast.LENGTH_LONG).show()
                    } else {
                        recipeRepository.getMealDetails(meal!!.idMeal) { ingredients, instructions ->
                            val ingredientsDescription = ingredients?.joinToString(", ") ?: "No ingredients found"

                            val dbHelper = DbHelper(this@AddRecipeActivity, null)
                            val userId = getCurrentUserId()
                            val newItem = Item(0, imageUrl, title, ingredientsDescription, description, instructions ?: "")
                            dbHelper.addItem(userId, newItem)

                            Toast.makeText(this@AddRecipeActivity, "Recipe added successfully", Toast.LENGTH_LONG).show()
                            val intent = Intent(this@AddRecipeActivity, ItemsActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }
        }
    }

    private fun getCurrentUserId(): Int {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("user_id", -1)
    }
}
