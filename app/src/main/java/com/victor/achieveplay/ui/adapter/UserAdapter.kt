package com.victor.achieveplay.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.victor.achieveplay.R
import com.victor.achieveplay.data.model.User
import com.victor.achieveplay.ui.view.OtherUserProfile

class UserAdapter(
    private val users: List<User>
) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImageView: ImageView = itemView.findViewById(R.id.user_profile_image)
        val userNameTextView: TextView = itemView.findViewById(R.id.user_name_text)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val user = users[position]
                    val intent = Intent(itemView.context, OtherUserProfile::class.java).apply {
                        putExtra("USER_ID", user.userId)
                    }
                    itemView.context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.userNameTextView.text = user.userName
        Glide.with(holder.itemView.context)
            .load(user.photoUrl)
            .into(holder.profileImageView)
    }

    override fun getItemCount(): Int = users.size
}
