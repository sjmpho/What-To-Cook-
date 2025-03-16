package com.example.whattocook.ContainedFragments


import android.R
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whattocook.APIServices.SpoonacularApiService
import com.example.whattocook.Models.RecipesAdapter
import com.example.whattocook.Models.Utility

import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.core.view.isEmpty
import com.example.whattocook.ScanActivity
import com.example.whattocook.containedActivity


class TextFragment : Fragment() {

    lateinit var ingredient_input : EditText
    lateinit var ingridents_layout :LinearLayout
    lateinit var Sublit:Button
    lateinit var ingredients_list : ArrayList<String>
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
        var view =  inflater.inflate(com.example.whattocook.R.layout.fragment_text, container, false)

        ingredient_input = view.findViewById(com.example.whattocook.R.id.ingredients_input)
        Sublit = view.findViewById(com.example.whattocook.R.id.submit_btn)
        recipesRecyclerView = view.findViewById(com.example.whattocook.R.id.LL_contain_suggestions)
        floatingActionButton = view.findViewById(com.example.whattocook.R.id.toScanPage)
        recipesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        recipesAdapter = RecipesAdapter(emptyList(),requireContext()) // Initialize with empty list
        recipesRecyclerView.adapter = recipesAdapter
        ingridents_layout = view.findViewById(com.example.whattocook.R.id.ll_layout)

        ingredients_list = arrayListOf()

        floatingActionButton.setOnClickListener{

            GotoContained()
        }

        Sublit.setOnClickListener{

            ingredients_str = ingredient_input.text.toString()
            if(!ingredients_str.isEmpty()){

                addWordLayout(ingredients_str)
               // findRecipes(ingredients_str)

                ingredient_input.setText("")
            }else{

            }

        }

        return view
    }
    private fun GotoContained(){

        val intent = Intent(requireContext(), ScanActivity ::class.java)
        startActivity(intent)

    }
    fun addWordLayout(word: String?) {

        val currentRow = getCurrentRow(ingridents_layout)


        val inflater = LayoutInflater.from(requireContext())
        val wordView: View = inflater.inflate(com.example.whattocook.R.layout.ingredient_box, currentRow, false)


        val wordText = wordView.findViewById<TextView>(com.example.whattocook.R.id.ingredient_name)
        wordText.text = word
        ingredients_list.add(word.toString())
        findRecipes(ConvertToString())
        Log.d("dagger", "addWordLayout: aray ${ConvertToString()}")

        val removeButton = wordView.findViewById<ImageButton>(com.example.whattocook.R.id.remove_ingredient_btn)
        removeButton.setOnClickListener {
            val wordText = wordView.findViewById<TextView>(com.example.whattocook.R.id.ingredient_name)
            ingredients_list.remove(wordText.text)

            Log.d("dagger", "addWordLayout: aray ${ConvertToString()}")
            findRecipes(ConvertToString())
            currentRow.removeView(wordView)

            if (currentRow.isEmpty()) {

                ingridents_layout.removeView(currentRow)
            }
        }


        currentRow.addView(wordView)
    }
    @SuppressLint("SuspiciousIndentation")
    private fun ConvertToString(): String{
        var words : String = ""

        for (word : String in ingredients_list)
        {
            if(words.isEmpty())
                words = word
            else
            words += ","+word
        }


        return words
    }


    private fun getCurrentRow(parentLayout: LinearLayout): LinearLayout {
        if (parentLayout.isEmpty()) {

            return createNewRow(parentLayout)
        }


        val lastRow = parentLayout.getChildAt(parentLayout.childCount - 1) as LinearLayout


        return if (lastRow.childCount < 4) {
            lastRow
        } else {

            createNewRow(parentLayout)
        }
    }


    private fun createNewRow(parentLayout: LinearLayout): LinearLayout {
        val newRow = LinearLayout(requireContext())
        newRow.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        newRow.orientation = LinearLayout.HORIZONTAL
        parentLayout.addView(newRow)
        return newRow
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