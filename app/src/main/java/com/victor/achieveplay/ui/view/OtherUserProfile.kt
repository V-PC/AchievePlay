package com.victor.achieveplay.ui.view

import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.victor.achieveplay.R
import com.victor.achieveplay.data.model.User
import com.victor.achieveplay.data.model.Game
import com.victor.achieveplay.ui.adapter.GameAdapter

class OtherUserProfile : AppCompatActivity() {

    private lateinit var profileImageView: ImageView
    private lateinit var usernameTextView: TextView
    private lateinit var userListsRecyclerView: RecyclerView
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_user_profile)

        profileImageView = findViewById(R.id.profile_image)
        usernameTextView = findViewById(R.id.username_text_view)
        userListsRecyclerView = findViewById(R.id.user_lists_recyclerview)

        val userId = intent.getStringExtra("USER_ID")
        if (userId != null) {
            loadUserProfile(userId)
            loadUserLists(userId)
        }

        applyAnimations()
    }

    private fun loadUserProfile(userId: String) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val user = document.toObject(User::class.java)
                    user?.let {
                        usernameTextView.text = it.userName
                        Glide.with(this)
                            .load(it.photoUrl)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(profileImageView)
                    }
                }
            }
            .addOnFailureListener { exception ->
                // Manejar el error
            }
    }

    private fun loadUserLists(userId: String) {
        db.collection("lists")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                val lists = documents.map { it.toObject(Game::class.java) }
                userListsRecyclerView.layoutManager = LinearLayoutManager(this)
                userListsRecyclerView.adapter = GameAdapter(lists) { game ->
                    // Handle game click if needed
                }

                val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
                userListsRecyclerView.startAnimation(fadeIn)
            }
            .addOnFailureListener { exception ->
                // Manejar el error
            }
    }

    private fun applyAnimations() {
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        profileImageView.startAnimation(fadeIn)
        usernameTextView.startAnimation(fadeIn)
    }
}
