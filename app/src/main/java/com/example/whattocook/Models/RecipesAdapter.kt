package com.example.whattocook.Models

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.whattocook.R
import com.example.whattocook.ViewRecipe
import com.example.whattocook.containedActivity
import com.google.firebase.Timestamp
import kotlinx.coroutines.newSingleThreadContext
import java.util.Objects

class RecipesAdapter (private val recipes: List<Recipe>,context :Context) : RecyclerView.Adapter<RecipesAdapter.RecipeViewHolder>() {
val context : Context = context;

        inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val recipeImage: ImageView = itemView.findViewById(R.id.recipeImage)
            private val recipeTitle: TextView = itemView.findViewById(R.id.recipeTitle)
            private val recipeIngredients: TextView = itemView.findViewById(R.id.recipeIngredients)
            private val saveBookMark : ImageView = itemView.findViewById(R.id.save_bookmark)
            private val mark_favourite : ImageView = itemView.findViewById(R.id.mark_favourite)


            fun bind(recipe: Recipe) {
                Glide.with(itemView.context).load(recipe.image).into(recipeImage)

                mark_favourite.setOnClickListener {


                  val recip = createModel(recipe)
                    Utility.getFavourites().add(recip).addOnSuccessListener {
                        Toast.makeText(context, "Recipe is bookmarked", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener { Exception ->
                        Log.d("dagger", "bind: ${Exception.message}")
                        Toast.makeText(context, "bookmark failed ${Exception}", Toast.LENGTH_SHORT).show()
                    }

                }
                saveBookMark.setOnClickListener {
                    val recip = createModel(recipe)
                    Utility.getBookmarked().add(recip).addOnSuccessListener {
                        Toast.makeText(context, "Recipe is Marked as favourite", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener { Exception ->
                        Log.d("dagger", "bind: ${Exception.message}")
                        Toast.makeText(context, " favourite failed ${Exception}", Toast.LENGTH_SHORT).show()
                    }
                }
                recipeTitle.text = recipe.title
                recipeIngredients.text = "Uses ${recipe.usedIngredientCount} ingredients, missing ${recipe.missedIngredientCount}"


            }
        }
    fun createModel(recipe: Recipe): HashMap<String, Any> {
        val recip = HashMap<String, Any>()
        recip.put("id" , recipe.id)
        recip.put("title" , recipe.title)
            recip.put("image" ,recipe.image)
            recip.put("ingredientCount" , recipe.usedIngredientCount +recipe.missedIngredientCount)
            recip.put("timeStamp",Timestamp.now())

        return recip
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