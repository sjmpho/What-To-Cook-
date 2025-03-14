package com.example.whattocook.Models

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.whattocook.R
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class SavedRecipesAdapter (options: FirestoreRecyclerOptions<firebaseReciepeDetails>) : FirestoreRecyclerAdapter<firebaseReciepeDetails, SavedRecipesAdapter.ItemViewHolder>(options) {

    // Inner ViewHolder class
    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.recipeTitle)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.recipeIngredients)
        private val image :ImageView = itemView.findViewById(R.id.recipeImage)
        private val favourite : ImageView = itemView.findViewById(R.id.mark_favourite)
        private val bookMark : ImageView = itemView.findViewById(R.id.save_bookmark)


        fun bind(item: firebaseReciepeDetails) {
            nameTextView.text = item.title
            descriptionTextView.text = item.ingredientCount.toString()
            Glide.with(itemView.context).load(item.image).into(image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int, model: firebaseReciepeDetails) {
        holder.bind(model)
    }
}
