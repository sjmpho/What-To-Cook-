package com.example.whattocook.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whattocook.Models.SavedRecipesAdapter
import com.example.whattocook.Models.Utility
import com.example.whattocook.Models.firebaseReciepeDetails
import com.example.whattocook.R
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class FavouritesFragment : Fragment() {

    // Use nullable View instead of lateinit
    lateinit var Recyclerview : RecyclerView
    lateinit var adapter : SavedRecipesAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_favourites, container, false)
        Recyclerview = view.findViewById(R.id.recyclerVIew)
        Recyclerview.layoutManager = LinearLayoutManager(requireContext())


        val query = Utility.getFavourites()

        // FirestoreRecyclerOptions

        val options = FirestoreRecyclerOptions.Builder<firebaseReciepeDetails>()
            .setQuery(query, firebaseReciepeDetails::class.java)
            .build()
        adapter = SavedRecipesAdapter(options)
        Recyclerview.adapter = adapter

        return view
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize views and handle logic here
        // Example: view.findViewById<TextView>(R.id.textView).text = "Favourites"
    }
}

