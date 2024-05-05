package com.victor.achieveplay

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GameDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_details)

        val gameName = intent.getStringExtra("game_name")
        val gameGenre = intent.getStringExtra("game_genre")
        val gamePlatform = intent.getStringExtra("game_platform")
        val gameReleaseDate = intent.getStringExtra("game_release_date")

        findViewById<TextView>(R.id.details_name).text = gameName
        findViewById<TextView>(R.id.details_genre).text = gameGenre
        findViewById<TextView>(R.id.details_platform).text = gamePlatform
        findViewById<TextView>(R.id.details_release_date).text = gameReleaseDate
    }
}
