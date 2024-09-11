package com.example.recipeofthedishes

data class User(val login: String,val email: String,val password: String,val items: List<Item> = listOf()
)

