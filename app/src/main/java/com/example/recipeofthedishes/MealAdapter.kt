import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipeofthedishes.DbHelper
import com.example.recipeofthedishes.Item
import com.example.recipeofthedishes.R
import com.example.recipeofthedishes.TranslateResponse
import com.example.recipeofthedishes.TranslationCallback
import data.CookbookModels
import networks.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale

class MealAdapter : RecyclerView.Adapter<MealAdapter.MealViewHolder>() {
    private var meals: List<CookbookModels.Meal> = listOf()
    private lateinit var translateApi: FreeTranslateAPI

    fun setMeals(meals: List<CookbookModels.Meal>) {
        this.meals = meals
        notifyDataSetChanged()
        Log.d("MealAdapter", "Meals set: ${meals.map { it.strMeal }}")
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
        val retrofit = Retrofit.Builder()
            .baseUrl("https://ftapi.pythonanywhere.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        translateApi = retrofit.create(FreeTranslateAPI::class.java)
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
                                Log.d(
                                    "Translation",
                                    "Translated Text: ${responseBody.destinationText}"
                                )
                                callback.onTranslationCompleted(responseBody.destinationText)
                            }

                            responseBody?.translations?.allTranslations != null -> {
                                Log.d(
                                    "Translation",
                                    "All Translations: ${responseBody.translations.allTranslations}"
                                )
                                val firstTranslation =
                                    responseBody.translations.allTranslations[0][0]
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
                Log.e("Translation", "Error: ${t.message}")
                callback.onTranslationFailed("Error: ${t.message}")
            }
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.meal_adapter, parent, false)
        return MealViewHolder(view)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        val meal = meals[position]
        Log.d("MealViewHolder", "Binding meal: ${meal.strMeal}, ${meal.strYoutube}")
        holder.bind(meal)
    }

    override fun getItemCount(): Int = meals.size

    class MealViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mealImage: ImageView = itemView.findViewById(R.id.mealImage)
        private val mealName: TextView = itemView.findViewById(R.id.mealName)

        fun bind(meal: CookbookModels.Meal) {
            mealName.text = meal.strMeal
            Glide.with(itemView.context).load(meal.strMealThumb).into(mealImage)
            Log.d("MealViewHolder", "Binding meal: ${meal.strMeal}, ${meal.strInstructions}")
        }
    }

    private fun getCurrentUserId(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("user_id", -1)
    }

    val itemTouchHelperCallback =
        object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val meal = meals[position]

                RetrofitClient.instance.lookupMealById(meal.idMeal)
                    .enqueue(object : Callback<CookbookModels.MealResponse> {
                        override fun onResponse(
                            call: Call<CookbookModels.MealResponse>,
                            response: Response<CookbookModels.MealResponse>
                        ) {
                            if (response.isSuccessful) {
                                val fetchedMeal = response.body()?.meals?.firstOrNull()
                                fetchedMeal?.let {
                                    val ingredients = listOf(
                                        it.strIngredient1 to it.strMeasure1,
                                        it.strIngredient2 to it.strMeasure2,
                                        it.strIngredient3 to it.strMeasure3,
                                        it.strIngredient4 to it.strMeasure4,
                                        it.strIngredient5 to it.strMeasure5,
                                        it.strIngredient6 to it.strMeasure6,
                                        it.strIngredient7 to it.strMeasure7,
                                        it.strIngredient8 to it.strMeasure8,
                                        it.strIngredient9 to it.strMeasure9,
                                        it.strIngredient10 to it.strMeasure10,
                                        it.strIngredient11 to it.strMeasure11,
                                        it.strIngredient12 to it.strMeasure12,
                                        it.strIngredient13 to it.strMeasure13,
                                        it.strIngredient14 to it.strMeasure14,
                                        it.strIngredient15 to it.strMeasure15,
                                        it.strIngredient16 to it.strMeasure16,
                                        it.strIngredient17 to it.strMeasure17,
                                        it.strIngredient18 to it.strMeasure18,
                                        it.strIngredient19 to it.strMeasure19,
                                        it.strIngredient20 to it.strMeasure20
                                    ).mapNotNull { (ingredient, measure) ->
                                        ingredient?.takeIf { it.isNotEmpty() }
                                            ?.let { it to (measure ?: "") }
                                    }

                                    val dbHelper = DbHelper(viewHolder.itemView.context, null)
                                    val userId = getCurrentUserId(viewHolder.itemView.context)
                                    val instructions =
                                        it.strInstructions ?: "No instructions available"

                                    val currentLanguage = Locale.getDefault().language
                                    val targetLanguage = when (currentLanguage) {
                                        "ru" -> "ru"
                                        "be" -> "be"
                                        else -> "en"
                                    }

                                    translateText(
                                        "en",
                                        targetLanguage,
                                        it.strMeal,
                                        object : TranslationCallback {
                                            override fun onTranslationCompleted(translatedText: String) {
                                                it.strMeal = decodeUnicode(translatedText)
                                                translateText(
                                                    "en",
                                                    targetLanguage,
                                                    instructions,
                                                    object : TranslationCallback {
                                                        override fun onTranslationCompleted(
                                                            translatedInstructions: String
                                                        ) {
                                                            val translatedIngredients =
                                                                mutableListOf<Pair<String, String>>()
                                                            var ingredientsTranslated = 0

                                                            ingredients.forEach { (ingredient, measure) ->
                                                                translateText(
                                                                    "en",
                                                                    targetLanguage,
                                                                    ingredient,
                                                                    object : TranslationCallback {
                                                                        override fun onTranslationCompleted(
                                                                            translatedIngredient: String
                                                                        ) {
                                                                            translatedIngredients.add(
                                                                                decodeUnicode(
                                                                                    translatedIngredient
                                                                                ) to measure
                                                                            )
                                                                            ingredientsTranslated++
                                                                            if (ingredientsTranslated == ingredients.size) {
                                                                                val newItem = Item(
                                                                                    0,
                                                                                    it.strMealThumb,
                                                                                    it.strMeal,
                                                                                    translatedIngredients.joinToString { "${it.first}: ${it.second}" },
                                                                                    "",
                                                                                    decodeUnicode(
                                                                                        translatedInstructions
                                                                                    ),
                                                                                    it.strYoutube.toString()
                                                                                )
                                                                                dbHelper.addItem(
                                                                                    userId,
                                                                                    newItem
                                                                                )
                                                                                Toast.makeText(
                                                                                    viewHolder.itemView.context,
                                                                                    "Recipe added successfully",
                                                                                    Toast.LENGTH_LONG
                                                                                ).show()
                                                                                meals =
                                                                                    meals.toMutableList()
                                                                                        .apply {
                                                                                            removeAt(
                                                                                                position
                                                                                            )
                                                                                        }
                                                                                notifyItemRemoved(
                                                                                    position
                                                                                )
                                                                            }
                                                                        }

                                                                        override fun onTranslationFailed(
                                                                            errorMessage: String
                                                                        ) {
                                                                            Toast.makeText(
                                                                                viewHolder.itemView.context,
                                                                                "Translation failed: $errorMessage",
                                                                                Toast.LENGTH_LONG
                                                                            ).show()
                                                                            notifyItemChanged(
                                                                                position
                                                                            )
                                                                        }
                                                                    }
                                                                )
                                                            }
                                                        }

                                                        override fun onTranslationFailed(
                                                            errorMessage: String
                                                        ) {
                                                            Toast.makeText(
                                                                viewHolder.itemView.context,
                                                                "Translation failed: $errorMessage",
                                                                Toast.LENGTH_LONG
                                                            ).show()
                                                            notifyItemChanged(position)
                                                        }
                                                    }
                                                )
                                            }

                                            override fun onTranslationFailed(errorMessage: String) {
                                                Toast.makeText(
                                                    viewHolder.itemView.context,
                                                    "Translation failed: $errorMessage",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                notifyItemChanged(position)
                                            }
                                        }
                                    )
                                }
                            } else {
                                Toast.makeText(
                                    viewHolder.itemView.context,
                                    "Failed to fetch recipe",
                                    Toast.LENGTH_LONG
                                ).show()
                                notifyItemChanged(position)
                            }
                        }

                        override fun onFailure(
                            call: Call<CookbookModels.MealResponse>,
                            t: Throwable
                        ) {
                            Toast.makeText(
                                viewHolder.itemView.context,
                                "Error: ${t.message}",
                                Toast.LENGTH_LONG
                            ).show()
                            notifyItemChanged(position)
                        }
                    })
            }
        }
    val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
}