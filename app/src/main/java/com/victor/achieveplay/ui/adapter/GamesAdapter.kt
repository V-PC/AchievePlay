package com.victor.achieveplay.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.victor.achieveplay.R
import com.victor.achieveplay.data.model.Game

class GameAdapter(private var gameList: List<Game> = listOf(), private val onGameClick: (Game) -> Unit) : RecyclerView.Adapter<GameAdapter.GameViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_game, parent, false)
        return GameViewHolder(itemView, onGameClick)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game = gameList[position]
        holder.bind(game)
    }

    override fun getItemCount(): Int = gameList.size



    class GameViewHolder(itemView: View, private val onGameClick: (Game) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val gameImage: ImageView = itemView.findViewById(R.id.image_game)
        private val gameName: TextView = itemView.findViewById(R.id.text_game_name)

        fun bind(game: Game) {
            gameName.text = game.name
            Glide.with(itemView.context)
                .load(game.image)
                .into(gameImage)
            itemView.setOnClickListener { onGameClick(game) }
        }
    }
}


