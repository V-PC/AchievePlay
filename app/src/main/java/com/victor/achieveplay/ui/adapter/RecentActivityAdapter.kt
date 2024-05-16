package com.victor.achieveplay.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.victor.achieveplay.R
import com.victor.achieveplay.data.model.RecentActivityModel

class RecentActivityAdapter(private val recentActivity: List<RecentActivityModel>) :
    RecyclerView.Adapter<RecentActivityAdapter.RecentActivityViewHolder>() {

    class RecentActivityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val activityTextView: TextView = itemView.findViewById(R.id.activityTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentActivityViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recent_activity, parent, false)
        return RecentActivityViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecentActivityViewHolder, position: Int) {
        val activity = recentActivity[position]
        holder.activityTextView.text = activity.description
    }

    override fun getItemCount() = recentActivity.size
}