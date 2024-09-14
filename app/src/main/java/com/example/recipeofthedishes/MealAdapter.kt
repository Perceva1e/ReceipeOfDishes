import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipeofthedishes.R
import data.CookbookModels

class MealAdapter : RecyclerView.Adapter<MealAdapter.MealViewHolder>() {

    private var meals: List<CookbookModels.Meal> = listOf()

    fun setMeals(meals: List<CookbookModels.Meal>) {
        this.meals = meals
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.meal_adapter, parent, false)
        return MealViewHolder(view)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        val meal = meals[position]
        holder.bind(meal)
    }

    override fun getItemCount(): Int = meals.size

    class MealViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mealImage: ImageView = itemView.findViewById(R.id.mealImage)
        private val mealName: TextView = itemView.findViewById(R.id.mealName)

        fun bind(meal: CookbookModels.Meal) {
            mealName.text = meal.strMeal
            Glide.with(itemView.context).load(meal.strMealThumb).into(mealImage)
        }
    }
}
