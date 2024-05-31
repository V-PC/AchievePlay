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

class GamesInListAdapter(
    private val games: List<Game>,
    private val onDeleteClick: (Game) -> Unit
) : RecyclerView.Adapter<GamesInListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val gameCoverImageView: ImageView = itemView.findViewById(R.id.game_cover_image_view)
        val gameNameTextView: TextView = itemView.findViewById(R.id.game_name_text_view)
        val deleteButton: ImageView = itemView.findViewById(R.id.delete_button)

        init {
            deleteButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onDeleteClick(games[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.game_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val game = games[position]
        holder.gameNameTextView.text = game.name
        Glide.with(holder.itemView.context)
            .load(game.image)
            .into(holder.gameCoverImageView)
    }

    override fun getItemCount(): Int = games.size
}
