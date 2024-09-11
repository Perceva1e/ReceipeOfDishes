package networks

import data.CookbookModels
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class RecipeRepository {
    suspend fun loadMealImage(mealId: String): CookbookModels.Meal? {
        return suspendCancellableCoroutine { continuation ->
            val call = RetrofitClient.instance.lookupMealById(mealId)
            call.enqueue(object : Callback<CookbookModels.MealResponse> {
                override fun onResponse(
                    call: Call<CookbookModels.MealResponse>,
                    response: Response<CookbookModels.MealResponse>
                ) {
                    if (response.isSuccessful) {
                        val meal = response.body()?.meals?.firstOrNull()
                        continuation.resume(meal)
                    } else {
                        continuation.resume(null)
                    }
                }
                override fun onFailure(call: Call<CookbookModels.MealResponse>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
            continuation.invokeOnCancellation {
                call.cancel()
            }
        }
    }

    suspend fun loadMealImageByName(mealName: String): CookbookModels.Meal? {
        return suspendCancellableCoroutine { continuation ->
            val call = RetrofitClient.instance.searchMealByName(mealName)
            call.enqueue(object : Callback<CookbookModels.MealResponse> {
                override fun onResponse(
                    call: Call<CookbookModels.MealResponse>,
                    response: Response<CookbookModels.MealResponse>
                ) {
                    if (response.isSuccessful) {
                        val meal = response.body()?.meals?.firstOrNull()
                        continuation.resume(meal)
                    } else {
                        continuation.resume(null)
                    }
                }
                override fun onFailure(call: Call<CookbookModels.MealResponse>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
            continuation.invokeOnCancellation {
                call.cancel()
            }
        }
    }
    fun getMealDetails(mealId: String, callback: (List<String>?, String?) -> Unit) {
        val call = RetrofitClient.instance.lookupMealById(mealId)
        call.enqueue(object : Callback<CookbookModels.MealResponse> {
            override fun onResponse(
                call: Call<CookbookModels.MealResponse>,
                response: Response<CookbookModels.MealResponse>
            ) {
                if (response.isSuccessful) {
                    val meal = response.body()?.meals?.firstOrNull()
                    val ingredients = meal?.let {
                        listOfNotNull(
                            it.strIngredient1, it.strIngredient2, it.strIngredient3,
                            it.strIngredient4, it.strIngredient5, it.strIngredient6,
                            it.strIngredient7, it.strIngredient8, it.strIngredient9,
                            it.strIngredient10, it.strIngredient11, it.strIngredient12,
                            it.strIngredient13, it.strIngredient14, it.strIngredient15,
                            it.strIngredient16, it.strIngredient17, it.strIngredient18,
                            it.strIngredient19, it.strIngredient20
                        ).filter { ingredient -> ingredient.isNotEmpty() }
                    }
                    val instructions = meal?.strInstructions
                    callback(ingredients, instructions)
                } else {
                    callback(null, null)
                }
            }
            override fun onFailure(call: Call<CookbookModels.MealResponse>, t: Throwable) {
                callback(null, null)
            }
        })
    }
}
