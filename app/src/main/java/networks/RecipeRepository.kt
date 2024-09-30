package networks

import android.util.Log
import data.CookbookModels
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class RecipeRepository {

    suspend fun loadMealImageByName(mealName: String): CookbookModels.Meal? {
        return suspendCancellableCoroutine { continuation ->
            val call = RetrofitClient.instance.searchMealByName(mealName)
            Log.d("Translation", "Translated in fun: $mealName")
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
}
