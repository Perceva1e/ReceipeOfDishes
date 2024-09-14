package networks


import data.CookbookModels
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RecipeApiService {
    @GET("search.php")
    fun searchMealByName(@Query("s") mealName: String): Call<CookbookModels.MealResponse>

    @GET("search.php")
    fun listMealsByFirstLetter(@Query("f") firstLetter: String): Call<CookbookModels.MealResponse>

    @GET("lookup.php")
    fun lookupMealById(@Query("i") id: String): Call<CookbookModels.MealResponse>

    @GET("random.php")
    fun getRandomMeal(): Call<CookbookModels.MealResponse>

    @GET("categories.php")
    fun listAllCategories(): Call<CookbookModels.CategoryResponse>

    @GET("filter.php")
    fun getMealsByIngredient(@Query("i") ingredient: String): Call<CookbookModels.MealResponse>
}

