package com.example.recipeofthedishes

import MealAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import data.CookbookModels
import networks.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FindByIngredient : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var mealAdapter: MealAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_find_by_ingredient)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val editTextIngredient: EditText = findViewById(R.id.editTextIngredient)
        val buttonSearch: Button = findViewById(R.id.buttonSearch)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        mealAdapter = MealAdapter()
        recyclerView.adapter = mealAdapter
        val buttonBack : Button =findViewById(R.id.buttonBack)
        buttonBack.setOnClickListener{
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
        RetrofitClient.instance.getMealsByIngredient(ingredient).enqueue(object : Callback<CookbookModels.MealResponse> {
            override fun onResponse(call: Call<CookbookModels.MealResponse>, response: Response<CookbookModels.MealResponse>) {
                if (response.isSuccessful) {
                    response.body()?.meals?.let {
                        mealAdapter.setMeals(it)
                    }
                } else {
                    Toast.makeText(this@FindByIngredient, "Failed to get meals", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CookbookModels.MealResponse>, t: Throwable) {
                Toast.makeText(this@FindByIngredient, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
