package com.victor.achieveplay.ui.view

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.victor.achieveplay.R
import com.victor.achieveplay.data.model.Game
import com.victor.achieveplay.data.model.User
import com.victor.achieveplay.ui.adapter.GameAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DiscoveryActivity : AppCompatActivity() {
    private lateinit var searchView: TextInputEditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var genreSpinner: AutoCompleteTextView
    private lateinit var platformSpinner: AutoCompleteTextView
    private lateinit var profileImageView: ImageView

    private val db: FirebaseFirestore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_discovery)

        initUI()

        val fetchGamesButton = findViewById<Button>(R.id.fetch_games_button)
        fetchGamesButton.setOnClickListener {
            val searchQuery = searchView.text.toString()
            searchGames(searchQuery)
        }

        val buttonChangeActivity = findViewById<ImageView>(R.id.btnChangeActivity)
        buttonChangeActivity.setOnClickListener {
            val intent = Intent(this, UserProfileActivity::class.java)
            startActivity(intent)
        }

        loadUserProfileImage()
    }

    private fun initUI() {
        searchView = findViewById(R.id.search_view)
        genreSpinner = findViewById(R.id.spinner_genre)
        platformSpinner = findViewById(R.id.spinner_platform)
        profileImageView = findViewById(R.id.btnChangeActivity)

        // Inicializar Spinners (Filtros)
        initSpinners()

        // Configurar RecyclerView
        recyclerView = findViewById(R.id.recycler_view_games)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
    }

    private fun initSpinners() {
        // Géneros manuales
        val genres = arrayOf("Todos", "Action", "Indie", "Adventure", "RPG", "Strategy", "Shooter", "Casual", "Simulation", "Puzzle", "Arcade", "Platformer", "Racing", "Massively Multiplayer", "Sports", "Fighting", "Family", "BoardGames", "Educational", "Card")
        // Plataformas manuales obtenidas de la API de RAWG
        val platforms = arrayOf(
            "Todas", "PC", "PlayStation 5", "PlayStation 4", "Xbox One", "Xbox Series S/X",
            "Nintendo Switch", "iOS", "Android", "Nintendo 3DS", "Nintendo DS", "Nintendo DSi",
            "macOS", "Linux", "Xbox 360", "Xbox", "PlayStation 3", "PlayStation 2", "PlayStation",
            "PS Vita", "PSP", "Wii U", "Wii", "GameCube", "Nintendo 64", "Game Boy Advance",
            "Game Boy Color", "Game Boy", "SNES", "NES", "Classic Macintosh", "Apple II",
            "Commodore / Amiga", "Atari 7800", "Atari 5200", "Atari 2600", "Atari Flashback",
            "Atari 8-bit", "Atari ST", "Atari Lynx", "Atari XEGS", "Genesis", "SEGA Saturn",
            "SEGA CD", "SEGA 32X", "SEGA Master System", "Dreamcast", "3DO", "Jaguar", "Game Gear", "Neo Geo", "Web"
        )

        // Configurar adaptadores
        val genreAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, genres)
        val platformAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, platforms)

        genreSpinner.setAdapter(genreAdapter)
        platformSpinner.setAdapter(platformAdapter)
    }

    private fun searchGames(searchQuery: String) {
        val selectedGenre = genreSpinner.text.toString()
        val selectedPlatform = platformSpinner.text.toString()

        db.collection("Videojuegos")
            .orderBy("name")
            .startAt(searchQuery)
            .endAt(searchQuery + '\uf8ff')
            .get()
            .addOnSuccessListener { documents ->
                val filteredGames = documents.map { it.toObject(Game::class.java) }
                    .filter { game ->
                        (selectedPlatform == "Todas" || game.platforms?.contains(selectedPlatform) == true) &&
                                (selectedGenre == "Todos" || game.genres?.any { genre -> genre.name == selectedGenre } == true)
                    }

                recyclerView.adapter = GameAdapter(filteredGames) { game ->
                    val intent = Intent(this, GameDetailsActivity::class.java)
                    intent.putExtra("GAME_ID", game.id)
                    intent.putExtra("GAME_NAME", game.name)
                    intent.putExtra("GAME_IMAGE_URL", game.image)
                    intent.putExtra("RATING", game.rating?.toString())
                    intent.putExtra("RELEASE_DATE", game.released)
                    intent.putExtra("GENRES", game.genres?.map { it.name }?.let { ArrayList(it) })
                    intent.putExtra("PLATFORMS", game.platforms?.let { ArrayList(it) })
                    startActivity(intent)
                }

                val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
                recyclerView.startAnimation(fadeIn)
            }
            .addOnFailureListener { exception ->
                Log.d("Firestore", "Error getting documents: ", exception)
            }
    }

    private fun loadUserProfileImage() {
        val userId =
            FirebaseAuth.getInstance().currentUser?.uid
        Log.d("Glide", "User Id: $userId")
        if (userId != null) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val user = document.toObject(User::class.java)
                        user?.let {
                            // Cargar la imagen de perfil desde Firebase Storage
                            val profileImageUrl = it.photoUrl
                            Log.d("Glide", "Profile Image URL: $profileImageUrl")
                            Glide.with(this)
                                .load(profileImageUrl)
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .listener(object : RequestListener<Drawable> {
                                    override fun onLoadFailed(
                                        e: GlideException?,
                                        model: Any?,
                                        target: Target<Drawable>?,
                                        isFirstResource: Boolean
                                    ): Boolean {
                                        Log.e("Glide", "Error loading image", e)
                                        return false
                                    }

                                    override fun onResourceReady(
                                        resource: Drawable?,
                                        model: Any?,
                                        target: Target<Drawable>?,
                                        dataSource: DataSource?,
                                        isFirstResource: Boolean
                                    ): Boolean {
                                        return false
                                    }
                                })
                                .into(profileImageView)
                        }
                    } else {
                        Log.d("Firestore", "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("Firestore", "Error getting documents: ", exception)
                }
        }
    }
}
