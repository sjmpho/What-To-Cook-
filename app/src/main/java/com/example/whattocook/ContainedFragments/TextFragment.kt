package com.example.whattocook.ContainedFragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whattocook.APIServices.SpoonacularApiService
import com.example.whattocook.Models.RecipesAdapter
import com.example.whattocook.Models.Utility
import com.example.whattocook.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TextFragment : Fragment() {

    lateinit var ingredient_input : EditText
    lateinit var Sublit:Button
    private lateinit var recipesRecyclerView: RecyclerView
    private  lateinit var recipesAdapter: RecipesAdapter
    lateinit var ingredients_str :String
    lateinit var floatingActionButton: FloatingActionButton

    private val spoonacularApiService: SpoonacularApiService by lazy {
        Retrofit.Builder().baseUrl("https://api.spoonacular.com/") // Spoonacular base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SpoonacularApiService::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view =  inflater.inflate(R.layout.fragment_text, container, false)

        ingredient_input = view.findViewById(R.id.ingredients_input)
        Sublit = view.findViewById(R.id.submit_btn)
        recipesRecyclerView = view.findViewById(R.id.LL_contain_suggestions)
        floatingActionButton = view.findViewById(R.id.toScanPage)
        recipesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        recipesAdapter = RecipesAdapter(emptyList(),requireContext()) // Initialize with empty list
        recipesRecyclerView.adapter = recipesAdapter

        Sublit.setOnClickListener{

            ingredients_str = ingredient_input.text.toString()
            if(!ingredients_str.isEmpty()){

                findRecipes(ingredients_str)
                ingredient_input.setText("")
            }else{

            }

        }

        return view
    }

    private fun findRecipes(ingredients: String) {

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = spoonacularApiService.findRecipesByIngredients(
                    ingredients = ingredients,
                    apiKey = Utility.ApiKey
                ).execute()

                if (response.isSuccessful) {
                    val recipes = response.body()
                    withContext(Dispatchers.Main) {
                        if (recipes != null && recipes.isNotEmpty()) {
                            Log.d("dagger", "findRecipes: should apear"+response.errorBody())
                           recipesAdapter = RecipesAdapter(recipes,requireContext())
                            recipesRecyclerView.adapter = recipesAdapter
                        } else {
                            Log.d("dagger", "findRecipes: eerror"+response.errorBody())
                            recipesAdapter = RecipesAdapter(emptyList(),requireContext())
                            recipesRecyclerView.adapter = recipesAdapter
                        }
                    }
                }else{
                    Log.d("dagger", "findRecipes: response not successful "+response.errorBody())
                }
            } catch (e: Exception) {
                Log.d("dagger", "findRecipes: error"+e.message)
                e.printStackTrace()
            }
        }
    }

}