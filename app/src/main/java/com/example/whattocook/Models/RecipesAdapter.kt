package com.example.whattocook.Models

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.whattocook.R
import com.example.whattocook.ViewRecipe
import com.example.whattocook.containedActivity

class RecipesAdapter (private val recipes: List<Recipe>,context :Context) : RecyclerView.Adapter<RecipesAdapter.RecipeViewHolder>() {
val context : Context = context;

        inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val recipeImage: ImageView = itemView.findViewById(R.id.recipeImage)
            private val recipeTitle: TextView = itemView.findViewById(R.id.recipeTitle)
            private val recipeIngredients: TextView = itemView.findViewById(R.id.recipeIngredients)

            fun bind(recipe: Recipe) {
                Glide.with(itemView.context)
                    .load(recipe.image)
                    .into(recipeImage)
                recipeTitle.text = recipe.title
                recipeIngredients.text = "Uses ${recipe.usedIngredientCount} ingredients, missing ${recipe.missedIngredientCount}"
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe, parent, false)
            return RecipeViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
            holder.bind(recipes[position])
            holder.itemView.setOnClickListener{
                recipes[position].id//pass to api to data

                val intent = Intent(context, ViewRecipe::class.java)
                intent.putExtra("Id", recipes[position].id)
                context.startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return recipes.size
        }
}