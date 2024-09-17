package com.example.recipeofthedishes

import FreeTranslateAPI
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
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale

class AddRecipeActivity : BaseActivity() {

    private val recipeRepository = RecipeRepository()
    private lateinit var translateApi: FreeTranslateAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_recipe)

        val editTextTitle: EditText = findViewById(R.id.editTextTitle)
        val editText: EditText = findViewById(R.id.editText)
        val buttonAddRecipe: Button = findViewById(R.id.buttonAddRecipe)
        val buttonBack: Button = findViewById(R.id.buttonBack)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://ftapi.pythonanywhere.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        translateApi = retrofit.create(FreeTranslateAPI::class.java)
        val sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val language = sharedPreferences.getString("My_Lang", "")

        buttonAddRecipe.setOnClickListener {
            val title = editTextTitle.text.toString().trim()
            val description = editText.text.toString().trim()
            if (title.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_LONG).show()
            } else {
                val currentLanguage = Locale.getDefault().language
                val targetLanguage = when (currentLanguage) {
                    "ru" -> "ru"
                    "be" -> "be"
                    else -> "en"
                }

                CoroutineScope(Dispatchers.Main).launch {
                    val meal = recipeRepository.loadMealImageByName(title)
                    if (meal != null) {
                        translateText("en", targetLanguage, meal.strMeal, object : TranslationCallback {
                            override fun onTranslationCompleted(translatedTitle: String) {
                                translateText("en", targetLanguage, description, object : TranslationCallback {
                                    override fun onTranslationCompleted(translatedDescription: String) {
                                        val ingredients = listOf(
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
                                        translateText("en", targetLanguage, ingredients.joinToString(", ") { "${it.first}: ${it.second}" }, object : TranslationCallback {
                                            override fun onTranslationCompleted(translatedIngredients: String) {
                                                translateLongText("en", targetLanguage, meal.strInstructions, object : TranslationCallback {
                                                    override fun onTranslationCompleted(translatedInstructions: String) {
                                                        processRecipe(
                                                            translatedTitle,
                                                            translatedDescription,
                                                            translatedIngredients,
                                                            translatedInstructions,
                                                            meal.strMealThumb,
                                                            meal.strYoutube.toString()
                                                        )
                                                    }

                                                    override fun onTranslationFailed(errorMessage: String) {
                                                        Toast.makeText(this@AddRecipeActivity, errorMessage, Toast.LENGTH_SHORT).show()
                                                    }
                                                })
                                            }

                                            override fun onTranslationFailed(errorMessage: String) {
                                                Toast.makeText(this@AddRecipeActivity, errorMessage, Toast.LENGTH_SHORT).show()
                                            }
                                        })
                                    }

                                    override fun onTranslationFailed(errorMessage: String) {
                                        Toast.makeText(this@AddRecipeActivity, errorMessage, Toast.LENGTH_SHORT).show()
                                    }
                                })
                            }

                            override fun onTranslationFailed(errorMessage: String) {
                                Toast.makeText(this@AddRecipeActivity, errorMessage, Toast.LENGTH_SHORT).show()
                            }
                        })
                    } else {
                        Toast.makeText(this@AddRecipeActivity, "Meal not found", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        buttonBack.setOnClickListener {
            finish()
        }
    }

    private fun processRecipe(
        title: String,
        description: String,
        ingredients: String,
        instruction: String,
        imageUrl: String,
        videoUrl: String
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            val dbHelper = DbHelper(this@AddRecipeActivity, null)
            val userId = getCurrentUserId()
            val newItem = Item(
                id = 0,
                image = imageUrl,
                title = title,
                ingredient = ingredients,
                desc = description,
                instructions = instruction,
                video = videoUrl
            )
            dbHelper.addItem(userId, newItem)

            Toast.makeText(this@AddRecipeActivity, "Recipe added successfully", Toast.LENGTH_LONG).show()
            val intent = Intent(this@AddRecipeActivity, ItemsActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun getCurrentUserId(): Int {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("user_id", -1)
    }

    private fun translateText(
        fromLang: String,
        toLang: String,
        text: String,
        callback: TranslationCallback
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = translateApi.translateText(fromLang, toLang, text).execute()
                if (response.isSuccessful) {
                    val translatedText = response.body()?.destinationText ?: ""
                    callback.onTranslationCompleted(translatedText)
                } else {
                    callback.onTranslationFailed("Translation failed")
                }
            } catch (e: Exception) {
                callback.onTranslationFailed(e.message ?: "An error occurred")
            }
        }
    }

    private fun translateLongText(
        fromLang: String,
        toLang: String,
        text: String,
        callback: TranslationCallback
    ) {
        val maxChunkSize = 500
        val chunks = text.chunked(maxChunkSize)
        val translatedChunks = mutableListOf<String>()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                for (chunk in chunks) {
                    val response = translateApi.translateText(fromLang, toLang, chunk).execute()
                    if (response.isSuccessful) {
                        val translatedText = response.body()?.destinationText ?: ""
                        translatedChunks.add(translatedText)
                    } else {
                        callback.onTranslationFailed("Translation failed")
                        return@launch
                    }
                }
                val fullTranslatedText = translatedChunks.joinToString(" ")
                callback.onTranslationCompleted(fullTranslatedText)
            } catch (e: Exception) {
                callback.onTranslationFailed(e.message ?: "An error occurred")
            }
        }
    }
}
