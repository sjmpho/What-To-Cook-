package com.example.whattocook.APIServices

import com.example.whattocook.Models.Recipe
import com.example.whattocook.Models.RecipeDetails
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SpoonacularApiService {

    @GET("recipes/findByIngredients") fun findRecipesByIngredients(
        @Query("ingredients") ingredients: String,
        @Query("number") number: Int = 5, // Number of recipes to return
        @Query("apiKey") apiKey: String
    ): Call<List<Recipe>>
    @GET("recipes/{id}/information")
    fun getRecipeDetails(
        @Path("id") recipeId: Int,
        @Query("apiKey") apiKey: String
    ): Call<RecipeDetails>
    @GET("recipes/{id}/information")
    fun getRecipeIngredients(
        @Path("id") recipeId: Int,
        @Query("apiKey") apiKey: String
    ): Call<RecipeDetails>

}