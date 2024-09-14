package com.example.recipeofthedishes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import networks.RecipeRepository

class AddRecipeActivity : BaseActivity() {

    private val recipeRepository = RecipeRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_recipe)

        val editTextTitle: EditText = findViewById(R.id.editTextTitle)
        val editText: EditText = findViewById(R.id.editText)
        val buttonAddRecipe: Button = findViewById(R.id.buttonAddRecipe)
        val buttonBack: Button = findViewById(R.id.buttonBack)

        buttonBack.setOnClickListener {
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
                    val videoUrl = meal?.strYoutube ?: ""
                    if (imageUrl.isEmpty()) {
                        Toast.makeText(this@AddRecipeActivity, "Image not found for the recipe", Toast.LENGTH_LONG).show()
                    } else {
                        recipeRepository.getMealDetails(meal!!.idMeal) { ingredients, instructions ->
                            val ingredientsDescription = listOf(
                                meal.strIngredient1 to meal.strMeasure1,
                                meal.strIngredient2 to meal.strMeasure2,
                                meal.strIngredient3 to meal.strMeasure3,
                                meal.strIngredient4 to meal.strMeasure4,
                                meal.strIngredient5 to meal.strMeasure5,
                                meal.strIngredient6 to meal.strMeasure6,
                                meal.strIngredient7 to meal.strMeasure7,
                                meal.strIngredient8 to meal.strMeasure8,
                                meal.strIngredient9 to meal.strMeasure9,
                                meal.strIngredient10 to meal.strMeasure10,
                                meal.strIngredient11 to meal.strMeasure11,
                                meal.strIngredient12 to meal.strMeasure12,
                                meal.strIngredient13 to meal.strMeasure13,
                                meal.strIngredient14 to meal.strMeasure14,
                                meal.strIngredient15 to meal.strMeasure15,
                                meal.strIngredient16 to meal.strMeasure16,
                                meal.strIngredient17 to meal.strMeasure17,
                                meal.strIngredient18 to meal.strMeasure18,
                                meal.strIngredient19 to meal.strMeasure19,
                                meal.strIngredient20 to meal.strMeasure20
                            ).filter { it.first != null && it.second != null && it.first!!.isNotEmpty() && it.second!!.isNotEmpty() }
                                .joinToString(", ") { "${it.first} (${it.second})" }

                            val dbHelper = DbHelper(this@AddRecipeActivity, null)
                            val userId = getCurrentUserId()
                            val newItem = Item(0, imageUrl, title, ingredientsDescription, description, instructions ?: "", videoUrl)
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
