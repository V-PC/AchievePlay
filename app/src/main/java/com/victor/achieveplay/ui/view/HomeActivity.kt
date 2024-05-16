package com.victor.achieveplay.ui.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.victor.achieveplay.R
import com.victor.achieveplay.data.model.RecentActivityModel
import com.victor.achieveplay.data.model.RecommendedGameModel
import com.victor.achieveplay.ui.adapter.RecentActivityAdapter
import com.victor.achieveplay.ui.adapter.RecommendedGamesAdapter
import com.victor.achieveplay.ui.view.DiscoveryActivity
import com.victor.achieveplay.ui.view.UserProfileActivity

class HomeActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val welcomeTextView = findViewById<TextView>(R.id.welcomeTextView)
        val recentActivityRecyclerView = findViewById<RecyclerView>(R.id.recentActivityRecyclerView)
        val recommendedGamesRecyclerView = findViewById<RecyclerView>(R.id.recommendedGamesRecyclerView)
        val searchGamesButton = findViewById<Button>(R.id.searchGamesButton)
        val viewListsButton = findViewById<Button>(R.id.viewListsButton)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val user = auth.currentUser
        if (user != null) {
            welcomeTextView.text = "Welcome, ${user.displayName}!"

            // Load recent activity
            recentActivityRecyclerView.layoutManager = LinearLayoutManager(this)
            loadRecentActivity(recentActivityRecyclerView)

            // Load recommended games
            recommendedGamesRecyclerView.layoutManager = LinearLayoutManager(this)
            loadRecommendedGames(recommendedGamesRecyclerView)
        }

        searchGamesButton.setOnClickListener {
            val intent = Intent(this, DiscoveryActivity::class.java)
            startActivity(intent)
        }

        viewListsButton.setOnClickListener {
            val intent = Intent(this, UserProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadRecentActivity(recentActivityRecyclerView: RecyclerView) {
        val user = auth.currentUser
        val userId = user?.uid

        if (userId != null) {
            firestore.collection("gamesInList")
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnSuccessListener { documents ->
                    val recentActivity = documents.map { document ->
                        RecentActivityModel(
                            description = document.getString("description") ?: "",
                            timestamp = document.getLong("timestamp") ?: 0
                        )
                    }
                    val adapter = RecentActivityAdapter(recentActivity)
                    recentActivityRecyclerView.adapter = adapter
                }
                .addOnFailureListener { exception ->
                    // Handle the error
                }
        }
    }

    private fun loadRecommendedGames(recommendedGamesRecyclerView: RecyclerView) {
        firestore.collection("recommendedGames")
            .get()
            .addOnSuccessListener { documents ->
                val recommendedGames = documents.map { document ->
                    RecommendedGameModel(
                        name = document.getString("name") ?: "",
                        imageUrl = document.getString("imageUrl") ?: "",
                        description = document.getString("description") ?: "",
                        rating = document.getDouble("rating")?.toFloat() ?: 0f
                    )
                }
                val adapter = RecommendedGamesAdapter(recommendedGames)
                recommendedGamesRecyclerView.adapter = adapter
            }
            .addOnFailureListener { exception ->
                // Handle the error
            }
    }
}
