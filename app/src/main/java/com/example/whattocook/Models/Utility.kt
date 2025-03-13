package com.example.whattocook.Models

import com.google.firebase.Firebase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.firestore

public  class Utility {

    companion object{
        var  ApiKey :  String = "7a8a68f26c354c599836fb1b9ce60937"
        lateinit var UserID : String

        fun getFavourites(): CollectionReference {
            return Firebase.firestore.collection("Users").document(UserID).collection("Favourites")
        }
        fun getBookmarked(): CollectionReference {
            return Firebase.firestore.collection("Users").document(UserID).collection("Bookmarked")
        }
    }

}