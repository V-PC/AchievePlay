package com.victor.achieveplay.ui.view

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.victor.achieveplay.R
import java.util.Date

class GameDetailsActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_details)

        val gameName = intent.getStringExtra("GAME_NAME")
        val gameId = intent.getStringExtra("GAME_ID") // Asegúrate de pasar este ID desde la actividad anterior
        val gameImageUrl = intent.getStringExtra("GAME_IMAGE_URL")
        val releaseDate = intent.getStringExtra("RELEASE_DATE")
        val genre = intent.getStringExtra("GENRE")
        val rating = intent.getStringExtra("RATING")
        val description = intent.getStringExtra("DESCRIPTION")

        val textViewName = findViewById<TextView>(R.id.titleTextView)
        val imageViewGame = findViewById<ImageView>(R.id.gameImageView)
        val textViewReleaseDate = findViewById<TextView>(R.id.releaseDateTextView)
        val textViewGenre = findViewById<TextView>(R.id.genreTextView)
        val textViewRating = findViewById<TextView>(R.id.ratingTextView)
        val textViewDescription = findViewById<TextView>(R.id.descriptionTextView)
        val addToListButton = findViewById<Button>(R.id.addToListButton)
        val listsSpinner = findViewById<Spinner>(R.id.listsSpinner)
        val newListEditText = findViewById<EditText>(R.id.newListEditText)

        textViewName.text = gameName
        Glide.with(this).load(gameImageUrl).into(imageViewGame)
        textViewReleaseDate.text = releaseDate
        textViewGenre.text = genre
        textViewRating.text = rating
        textViewDescription.text = description

        firestore = FirebaseFirestore.getInstance()

        fetchUserLists(listsSpinner)

        addToListButton.setOnClickListener {
            val selectedList = listsSpinner.selectedItem?.toString()
            val newListName = newListEditText.text.toString().trim()

            if (newListName.isNotEmpty()) {
                createNewListAndAddGame(newListName, gameId, description)
            } else if (selectedList != null) {
                addToExistingList(selectedList, gameId, description)
            }
        }
    }

    private fun fetchUserLists(listsSpinner: Spinner) {
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid

        if (userId != null) {
            firestore.collection("lists")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { documents ->
                    val lists = mutableListOf<String>()
                    for (document in documents) {
                        lists.add(document.getString("listName")!!)
                    }
                    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, lists)
                    listsSpinner.adapter = adapter
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Failed to fetch lists: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun createNewListAndAddGame(listName: String, gameId: String?, gameDescription: String?) {
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid

        if (userId != null && gameId != null) {
            val newListRef = firestore.collection("lists").document()
            val newListData = hashMapOf(
                "userId" to userId,
                "listName" to listName,
                "listId" to newListRef.id
            )

            newListRef.set(newListData)
                .addOnSuccessListener {
                    addToGamesInList(newListRef.id, gameId, gameDescription)
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Failed to create list: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun addToExistingList(listName: String, gameId: String?, gameDescription: String?) {
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid

        if (userId != null && gameId != null) {
            firestore.collection("lists")
                .whereEqualTo("userId", userId)
                .whereEqualTo("listName", listName)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        val listId = documents.documents[0].getString("listId")
                        if (listId != null) {
                            addToGamesInList(listId, gameId, gameDescription)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Failed to find list: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun addToGamesInList(listId: String, gameId: String, gameDescription: String?) {
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid
        val gameInListRef = firestore.collection("gamesInList").document()
        val gameData = hashMapOf(
            "userId" to userId,
            "listId" to listId,
            "gameId" to gameId,
            "description" to gameDescription,
            "timestamp" to Date().time
        )

        gameInListRef.set(gameData)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Game added to list", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to add game to list", Toast.LENGTH_SHORT).show()
                }
            }
    }
}