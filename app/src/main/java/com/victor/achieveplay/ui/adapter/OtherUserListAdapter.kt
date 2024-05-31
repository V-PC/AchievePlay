package com.victor.achieveplay.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.victor.achieveplay.R

class OtherUserListAdapter(
    private val lists: List<Pair<String, String>>,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<OtherUserListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val listNameTextView: TextView = itemView.findViewById(R.id.list_name_text_view)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val listId = lists[position].first
                    onClick(listId)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listName = lists[position].second
        holder.listNameTextView.text = listName
    }

    override fun getItemCount(): Int = lists.size
}
