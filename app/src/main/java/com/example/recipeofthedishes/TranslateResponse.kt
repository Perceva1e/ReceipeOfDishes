import com.example.recipeofthedishes.TranslateResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface FreeTranslateAPI {
    @GET("/translate")
    fun translateText(
        @Query("sl") sourceLang: String?,
        @Query("dl") destLang: String,
        @Query("text") text: String
    ): Call<TranslateResponse>
}
