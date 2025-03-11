package com.example.whattocook.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.whattocook.APIServices.SpoonacularApiService
import com.example.whattocook.Models.RecipesAdapter
import com.example.whattocook.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class HomeFragment : Fragment() {
    private lateinit var ingredientsInput: EditText
    private lateinit var generateButton: Button
    private lateinit var recipesRecyclerView: RecyclerView
    private  lateinit var recipesAdapter: RecipesAdapter
    private val spoonacularApiService: SpoonacularApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.spoonacular.com/") // Spoonacular base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SpoonacularApiService::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recipesRecyclerView = view.findViewById(R.id.recycler)

        recipesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        recipesAdapter = RecipesAdapter(emptyList()) // Initialize with empty list
        recipesRecyclerView.adapter = recipesAdapter
        // Set click listener for the generate button

            findRecipes("duck")


        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun findRecipes(ingredients: String) {
        val apiKey = "7a8a68f26c354c599836fb1b9ce60937" // Replace with your API key

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = spoonacularApiService.findRecipesByIngredients(
                    ingredients = ingredients,
                    apiKey = apiKey
                ).execute()

                if (response.isSuccessful) {
                    val recipes = response.body()
                    withContext(Dispatchers.Main) {
                        if (recipes != null && recipes.isNotEmpty()) {
                          recipesAdapter = RecipesAdapter(recipes)
                            recipesRecyclerView.adapter = recipesAdapter
                        } else {
                            recipesAdapter = RecipesAdapter(emptyList())
                            recipesRecyclerView.adapter = recipesAdapter
                        }
                    }
                }
            } catch (e: Exception) {
                Log.d("dagger", "findRecipes: error"+e.message)
                e.printStackTrace()
            }
        }
            }


}