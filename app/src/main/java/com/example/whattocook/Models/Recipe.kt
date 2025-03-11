package com.example.whattocook.Models

data class Recipe(
    val id: Int,
    val title: String,
    val image: String,
    val usedIngredientCount: Int,
    val missedIngredientCount: Int
)