package com.example.recipeofthedishes

import FreeTranslateAPI
import MealAdapter
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import data.CookbookModels
import networks.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale

class FindByIngredient : BaseActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var mealAdapter: MealAdapter
    private lateinit var translateApi: FreeTranslateAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_find_by_ingredient)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val retrofit = Retrofit.Builder()
            .baseUrl("https://ftapi.pythonanywhere.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        translateApi = retrofit.create(FreeTranslateAPI::class.java)

        val editTextIngredient: EditText = findViewById(R.id.editTextIngredient)
        val buttonSearch: Button = findViewById(R.id.buttonSearch)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        mealAdapter = MealAdapter()
        recyclerView.adapter = mealAdapter
        val buttonBack: Button = findViewById(R.id.buttonBack)
        buttonBack.setOnClickListener {
            val intent = Intent(this, ItemsActivity::class.java)
            startActivity(intent)
        }
        buttonSearch.setOnClickListener {
            val ingredient = editTextIngredient.text.toString().trim()
            if (ingredient.isNotEmpty()) {
                fetchMeals(ingredient)
            } else {
                Toast.makeText(this, "Please enter an ingredient", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchMeals(ingredient: String) {
        val sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val language = sharedPreferences.getString("My_Lang", "")

        val currentLanguage = Locale.getDefault().language
        val targetLanguage = when (currentLanguage) {
            "ru" -> "en"
            "be" -> "en"
            else -> "en"
        }

        if (language == "ru" || language == "be") {
            translateText(language!!, targetLanguage, ingredient, object : TranslationCallback {
                override fun onTranslationCompleted(translatedText: String) {
                    searchMeals(translatedText)
                }

                override fun onTranslationFailed(errorMessage: String) {
                    searchMeals(ingredient)
                    Toast.makeText(this@FindByIngredient, errorMessage, Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            searchMeals(ingredient)
        }
    }

    private fun searchMeals(ingredient: String) {
        RetrofitClient.instance.getMealsByIngredient(ingredient)
            .enqueue(object : Callback<CookbookModels.MealResponse> {
                override fun onResponse(
                    call: Call<CookbookModels.MealResponse>,
                    response: Response<CookbookModels.MealResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.meals?.let { meals ->
                            val sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE)
                            val language = sharedPreferences.getString("My_Lang", "")
                            if (language == "ru" || language == "be") {
                                Log.d("Meals", "Translating meal names")
                                translateMealNames(meals, language!!)
                            } else {
                                Log.d("Meals", "Setting meals in adapter")
                                mealAdapter.setMeals(meals)
                            }
                        }
                    } else {
                        Log.e("Meals", "Failed to get meals")
                        Toast.makeText(this@FindByIngredient, "Failed to get meals", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<CookbookModels.MealResponse>, t: Throwable) {
                    Log.e("Meals", "Error: ${t.message}")
                    Toast.makeText(this@FindByIngredient, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun translateMealNames(meals: List<CookbookModels.Meal>, language: String) {
        val translatedMeals = mutableListOf<CookbookModels.Meal>()
        var completedRequests = 0

        meals.forEach { meal ->
            translateText("en", language, meal.strMeal, object : TranslationCallback {
                override fun onTranslationCompleted(translatedText: String) {
                    meal.strMeal = decodeUnicode(translatedText)
                    translatedMeals.add(meal)
                    completedRequests++
                    if (completedRequests == meals.size) {
                        runOnUiThread {
                            mealAdapter.setMeals(translatedMeals)
                        }
                    }
                }

                override fun onTranslationFailed(errorMessage: String) {
                    meal.strMeal = "Translation failed"
                    translatedMeals.add(meal)
                    completedRequests++
                    if (completedRequests == meals.size) {
                        runOnUiThread {
                            mealAdapter.setMeals(translatedMeals)
                        }
                    }
                    Toast.makeText(this@FindByIngredient, errorMessage, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun decodeUnicode(unicodeStr: String): String {
        return unicodeStr.replace("\\\\u([0-9A-Fa-f]{4})".toRegex()) {
            val charCode = it.groupValues[1].toInt(16)
            charCode.toChar().toString()
        }
    }

    private fun translateText(
        sourceLang: String,
        targetLang: String,
        text: String,
        callback: TranslationCallback
    ) {
        val call = translateApi.translateText(sourceLang, targetLang, text)
        call.enqueue(object : retrofit2.Callback<TranslateResponse> {
            override fun onResponse(
                call: retrofit2.Call<TranslateResponse>,
                response: retrofit2.Response<TranslateResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    Log.d("Translation", "Response Body: $responseBody")
                    try {
                        when {
                            responseBody?.destinationText != null -> {
                                Log.d("Translation", "Translated Text: ${responseBody.destinationText}")
                                callback.onTranslationCompleted(responseBody.destinationText)
                            }
                            responseBody?.translations?.allTranslations != null -> {
                                Log.d("Translation", "All Translations: ${responseBody.translations.allTranslations}")
                                val firstTranslation = responseBody.translations.allTranslations[0][0]
                                Log.d("Translation", "First Translated Text: $firstTranslation")
                                callback.onTranslationCompleted(firstTranslation.toString())
                            }
                            else -> {
                                Log.e("Translation", "Translation failed: Empty response")
                                callback.onTranslationFailed("Translation failed: Empty response")
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("Translation", "Error processing response: ${e.message}")
                        callback.onTranslationFailed("Error processing response: ${e.message}")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("Translation", "Translation failed: ${errorBody ?: "Unknown error"}")
                    callback.onTranslationFailed("Translation failed: ${errorBody ?: "Unknown error"}")
                }
            }

            override fun onFailure(call: retrofit2.Call<TranslateResponse>, t: Throwable) {
                Log.e("Translation", "Error: ${t.message ?: "Unknown error"}")
                callback.onTranslationFailed("Error: ${t.message ?: "Unknown error"}")
            }
        })
    }
}
