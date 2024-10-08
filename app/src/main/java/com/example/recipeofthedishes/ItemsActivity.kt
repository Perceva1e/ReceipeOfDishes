package com.example.recipeofthedishes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class ItemsActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_items)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val dbHelper = DbHelper(this, null)
        val userId = getCurrentUserId()

        CoroutineScope(Dispatchers.Main).launch {
            val items = dbHelper.getUserItems(userId)

            val itemList: RecyclerView = findViewById(R.id.itemList)
            itemList.layoutManager = LinearLayoutManager(this@ItemsActivity)
            itemList.adapter = ItemsAdapter(items.toMutableList(), this@ItemsActivity)
        }

        val buttonCreateRecipe: Button = findViewById(R.id.buttonCreateNewRecipe)
        buttonCreateRecipe.setOnClickListener {
            val intent = Intent(this, CreateSpecificRecipe::class.java)
            startActivity(intent)
        }

        val buttonAddNewRecipe: Button = findViewById(R.id.itemListButtonAdd)
        buttonAddNewRecipe.setOnClickListener {
            val intent = Intent(this, AddRecipeActivity::class.java)
            startActivity(intent)
        }
        val buttonFindByIngredient : Button = findViewById(R.id.findByIngredient)
        buttonFindByIngredient.setOnClickListener {
            val intent = Intent(this, FindByIngredient::class.java)
            startActivity(intent)
        }
        val buttonChangeLanguage : Button = findViewById(R.id.buttonChangeLanguage)
        buttonChangeLanguage.setOnClickListener {
            val currentLanguage = Locale.getDefault().language
            val newLanguage = when (currentLanguage) {
                "en" -> "ru"
                "ru" -> "be"
                else -> "en"
            }
            setLocale(newLanguage, this)
            recreate()
        }
    }

    private fun getCurrentUserId(): Int {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("user_id", -1)
    }
}
