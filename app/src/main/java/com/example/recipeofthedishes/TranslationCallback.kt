package com.example.recipeofthedishes

interface TranslationCallback {
    fun onTranslationCompleted(translatedText: String)
    fun onTranslationFailed(errorMessage: String)
}