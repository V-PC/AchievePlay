package com.victor.achieveplay

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.victor.achieveplay.R

class GameAdapter(private var gameList: List<Game> = listOf()) : RecyclerView.Adapter<GameAdapter.GameViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_game, parent, false)
        return GameViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game = gameList[position]
        holder.gameName.text = game.name
        Glide.with(holder.itemView.context)
            .load(game.image)
            .into(holder.gameImage)
    }

    override fun getItemCount(): Int = gameList.size

    fun setGames(games: List<Game>) {
        gameList = games
        notifyDataSetChanged()
    }

    class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val gameImage: ImageView = itemView.findViewById(R.id.image_game)
        val gameName: TextView = itemView.findViewById(R.id.text_game_name)
    }
}


