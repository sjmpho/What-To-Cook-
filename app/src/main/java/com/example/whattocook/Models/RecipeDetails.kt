package com.example.whattocook.Models

data class RecipeDetails (
    val id :Int,
    val title:String,
    val image:String,
    val readyInMinutes:Int,
    val ingredients : String,
    val instructions : String,
    val extendedIngredients: List<Ingredient>
)


