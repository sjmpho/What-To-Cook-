package com.example.whattocook

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.whattocook.APIServices.SpoonacularApiService
import com.example.whattocook.Models.RecipeDetails
import com.example.whattocook.Models.Utility
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ViewRecipe : AppCompatActivity() {
    lateinit var mealName : TextView
    lateinit var Instruction :TextView
    lateinit var Ingedients : TextView
    lateinit var image :ImageView
    lateinit var reciept : RecipeDetails

    private val spoonacularApiService: SpoonacularApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.spoonacular.com/") // Spoonacular base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SpoonacularApiService::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_recipe)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mealName = findViewById(R.id.view_name)
        Instruction = findViewById(R.id.view_instructions)
        Ingedients = findViewById(R.id.view_ingredients)
        image = findViewById(R.id.view_image)

        val RecID:Int = intent.getIntExtra("Id",0);



        val call1 = spoonacularApiService.getRecipeIngredients(RecID, Utility.ApiKey)

        call1.enqueue(object : Callback<RecipeDetails> {
            override fun onResponse(
                call: Call<RecipeDetails>,
                response: Response<RecipeDetails>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { displayIngredients(it) }
                } else {
                    Log.e("ERROR", "Failed to get ingredients")
                }
            }

            override fun onFailure(call: Call<RecipeDetails>, t: Throwable) {
                Log.e("ERROR", "API call failed: ${t.message}")
            }
        })



        val call = spoonacularApiService.getRecipeDetails(recipeId = RecID, apiKey = Utility.ApiKey)

        call.enqueue(object : Callback<RecipeDetails> {
            override fun onResponse(call: Call<RecipeDetails>, response: Response<RecipeDetails>) {
                if (response.isSuccessful) {
                    val recipeDetails = response.body()
                    if (recipeDetails != null) {
                        mealName.setText(recipeDetails.title)
                        val instructionsHtml = recipeDetails.instructions
                        Log.d("Instructions", "onResponse: "+ recipeDetails.instructions)
                        try {
                            val formattedInstructions = formatInstructions(recipeDetails.instructions)
                            Instruction.text = formattedInstructions
                            Log.d("Instructions", "onResponse: "+ formattedInstructions)
                        }catch(exception : Exception) {
                            Log.d("Instructions", "onResponse: exception $exception")
                        }

                        Glide.with(this@ViewRecipe)
                            .load(recipeDetails.image)
                            .into(image)
                    }
                }
            }

            override fun onFailure(call: Call<RecipeDetails>, t: Throwable) {
                Log.e("API_ERROR", "Failed to fetch recipe details", t)
            }
        })
    }
    fun formatInstructions(instructions: String): String {
        return if (instructions.contains("<li>")) {
            // Handle HTML format
            val doc = Jsoup.parse(instructions)
            doc.select("li").mapIndexed { index, element ->
                "${index + 1}. ${element.text().trim()}"
            }.joinToString("\n")
        } else {
            // Handle Plain Text format
            instructions.split(". ")
                .filter { it.isNotBlank() } // Remove empty lines
                .mapIndexed { index, step -> "${index + 1}. ${step.trim()}" }
                .joinToString("\n")
        }
    }
    fun displayIngredients(recipeIngredients: RecipeDetails) {
        val ingredientsText = recipeIngredients.extendedIngredients
            .joinToString("\n") {
                "${it.amount} ${it.unit} ${it.name}"
            }

        Ingedients.text = ingredientsText // Set ingredients list to TextView
    }

}