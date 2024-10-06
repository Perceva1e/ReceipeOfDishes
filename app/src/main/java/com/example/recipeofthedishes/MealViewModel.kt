package com.example.recipeofthedishes

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import data.CookbookModels

class MealViewModel : ViewModel() {
    val meals: MutableLiveData<List<CookbookModels.Meal>> = MutableLiveData()
}
