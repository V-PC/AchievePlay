package com.victor.achieveplay

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DiscoveryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_discovery)

        val searchButton = findViewById<Button>(R.id.search_button)
        val searchField = findViewById<EditText>(R.id.search_field)
        val gamesRecyclerView = findViewById<RecyclerView>(R.id.games_recycler_view)

        gamesRecyclerView.layoutManager = LinearLayoutManager(this)
        // Assuming you have a GamesAdapter and a ViewModel to handle data
        gamesRecyclerView.adapter = GamesAdapter()

        searchButton.setOnClickListener {
            val query = searchField.text.toString()
            if (query.isNotEmpty()) {
                // Perform search using the query
                searchGames(query)
            }
        }
    }

    private fun searchGames(query: String) {
        // Implement your search logic here, likely calling a ViewModel function
    }
}
