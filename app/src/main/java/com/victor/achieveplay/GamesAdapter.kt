package com.victor.achieveplay

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class Game(val name: String, val genre: String, val platform: String, val releaseDate: String)

class GameAdapter(private var games: List<Game>) : RecyclerView.Adapter<GameAdapter.GameViewHolder>() {

    inner class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.game_name)
        val genre: TextView = itemView.findViewById(R.id.game_genre)
        val platform: TextView = itemView.findViewById(R.id.game_platform)
        val releaseDate: TextView = itemView.findViewById(R.id.game_release_date)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val game = games[position]
                    val intent = Intent(itemView.context, GameDetailsActivity::class.java).apply {
                        putExtra("game_name", game.name)
                        putExtra("game_genre", game.genre)
                        putExtra("game_platform", game.platform)
                        putExtra("game_release_date", game.releaseDate)
                    }
                    itemView.context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_game, parent, false)
        return GameViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game = games[position]
        holder.name.text = game.name
        holder.genre.text = game.genre
        holder.platform.text = game.platform
        holder.releaseDate.text = game.releaseDate
    }

    override fun getItemCount(): Int = games.size

    fun updateGames(newGames: List<Game>) {
        games = newGames
        notifyDataSetChanged()
    }
}
