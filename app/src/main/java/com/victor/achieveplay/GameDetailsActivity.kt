package com.victor.achieveplay

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class GameDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_details)

        val gameName = intent.getStringExtra("GAME_NAME")
        val gameImageUrl = intent.getStringExtra("GAME_IMAGE_URL")

        val textViewName = findViewById<TextView>(R.id.titleTextView)
        val imageViewGame = findViewById<ImageView>(R.id.gameImageView)

        textViewName.text = gameName
        Glide.with(this).load(gameImageUrl).into(imageViewGame)
    }
}

