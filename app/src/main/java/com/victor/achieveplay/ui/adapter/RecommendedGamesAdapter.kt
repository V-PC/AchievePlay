package com.victor.achieveplay.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.victor.achieveplay.R
import com.victor.achieveplay.data.model.RecommendedGameModel

class RecommendedGamesAdapter(private val recommendedGames: List<RecommendedGameModel>) :
    RecyclerView.Adapter<RecommendedGamesAdapter.RecommendedGameViewHolder>() {

    class RecommendedGameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val gameImageView: ImageView = itemView.findViewById(R.id.gameImageView)
        val gameNameTextView: TextView = itemView.findViewById(R.id.gameNameTextView)
        val gameDescriptionTextView: TextView = itemView.findViewById(R.id.gameDescriptionTextView)
        val gameRatingTextView: TextView = itemView.findViewById(R.id.gameRatingTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendedGameViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recommended_game, parent, false)
        return RecommendedGameViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecommendedGameViewHolder, position: Int) {
        val game = recommendedGames[position]
        Glide.with(holder.itemView.context).load(game.imageUrl).into(holder.gameImageView)
        holder.gameNameTextView.text = game.name
        holder.gameDescriptionTextView.text = game.description
        holder.gameRatingTextView.text = "Rating: ${game.rating}"
    }

    override fun getItemCount() = recommendedGames.size
}
