package com.example.whattocook.Models

import com.google.firebase.Timestamp

data class firebaseReciepeDetails(
    var id: Int = 0, // Default value
    var title: String = "", // Default value
    var image: String = "", // Default value
    var timeStamp: Timestamp = Timestamp.now(), // Default value (current timestamp)
    var ingredientCount: Int = 0 // Default value
)

