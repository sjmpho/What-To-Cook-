package com.example.whattocook.Models

import android.app.DownloadManager.Query
import android.util.Log
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.whattocook.APIServices.SpoonacularApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Objects

class GroceryListAlogorithm {

    companion object{
private lateinit var HashOfFavourites : HashMap<String,Objects>
        private lateinit var HashOfBookmakerd : HashMap<String,Objects>
private lateinit var arrayOfFavourites : ArrayList<HashMap<String,Objects>>
        private lateinit var arrayOfBookMarked : ArrayList<HashMap<String,Objects>>
        private lateinit var ingrdents : HashMap<String,Objects>;
        private val spoonacularApiService: SpoonacularApiService by lazy {
            Retrofit.Builder()
                .baseUrl("https://api.spoonacular.com/") // Spoonacular base URL
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(SpoonacularApiService::class.java)
        }
        fun startAlgorithm(){

            arrayOfFavourites = arrayListOf()
            arrayOfBookMarked = arrayListOf()
            //retrivene favourites and book marked
            //retrive each of the ingedients of each meal.
            //
        fetchFavourites()
            fetchBookMarked()
        }

        private fun fetchFavourites() {
            // retrive favourites
            Utility.getFavourites().get().addOnSuccessListener {result ->
                for (document in result) {
                    Log.d("Firestore", "${document.id} => ${document.data}")
                    HashOfFavourites.clear()
                    HashOfFavourites.put("id", document.get("id") as Objects)
                    HashOfFavourites.put("timestamp", document.get("timeStamp") as Objects)
                    arrayOfFavourites.add(HashOfFavourites)
                }
            }
                .addOnFailureListener { exception ->
                    Log.w("Firestore", "Error getting documents: ", exception)
                }

            }

        private fun fetchBookMarked() {
            // retrive favourites
            Utility.getBookmarked().get().addOnSuccessListener {result ->
                for (document in result) {
                    Log.d("Firestore", "${document.id} => ${document.data}")
                    HashOfBookmakerd.clear()
                    HashOfBookmakerd.put("id", document.get("id") as Objects)
                    HashOfBookmakerd.put("timestamp", document.get("timeStamp") as Objects)
                    arrayOfBookMarked.add(HashOfBookmakerd)
                }
            }
                .addOnFailureListener { exception ->
                    Log.w("Firestore", "Error getting documents: ", exception)
                }

        }
        private fun getIngredients (mealID : Int){

            val call1 = spoonacularApiService.getRecipeIngredients(mealID, Utility.ApiKey)


        }
        }

}


