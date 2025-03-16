package com.example.whattocook.Models

import android.app.DownloadManager.Query
import android.util.Log
import androidx.lifecycle.viewmodel.CreationExtras
import java.util.Objects

class GroceryListAlogorithm {

    companion object{
private lateinit var HashOfFavourites : HashMap<String,Objects>
        private lateinit var HashOfBookmakerd : HashMap<String,Objects>
private lateinit var arrayOfFavourites : ArrayList<HashMap<String,Objects>>
        private lateinit var arrayOfBookMarked : ArrayList<HashMap<String,Objects>>

        fun startAlgorithm(){

            arrayOfFavourites = arrayListOf()
            //retrivene favourites and book marked
            //retrive each of the ingedients of each meal.
            //
        fetchFavourites()
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
        }
    private fun fetchBookMarked() {
        // retrive favourites
        Utility.getBookmarked().get().addOnSuccessListener {result ->
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
}


