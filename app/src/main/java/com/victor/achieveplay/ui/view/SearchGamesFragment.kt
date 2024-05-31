package com.victor.achieveplay.ui.view

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.victor.achieveplay.R
import com.victor.achieveplay.data.model.Game
import com.victor.achieveplay.data.model.User
import com.victor.achieveplay.ui.adapter.GameAdapter

class SearchGamesFragment : Fragment() {

    private lateinit var searchView: TextInputEditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var genreSpinner: AutoCompleteTextView
    private lateinit var platformSpinner: AutoCompleteTextView
    private lateinit var profileImageView: ImageView

    private val db: FirebaseFirestore = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search_games, container, false)
        initUI(view)
        return view
    }

    private fun initUI(view: View) {
        searchView = view.findViewById(R.id.search_view)
        genreSpinner = view.findViewById(R.id.spinner_genre)
        platformSpinner = view.findViewById(R.id.spinner_platform)
        profileImageView = view.findViewById(R.id.btnChangeActivity)

        // Inicializar Spinners (Filtros)
        initSpinners()

        // Configurar RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view_games)
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        val fetchGamesButton = view.findViewById<Button>(R.id.fetch_games_button)
        fetchGamesButton.setOnClickListener {
            val searchQuery = searchView.text.toString()
            searchGames(searchQuery)
        }

        loadUserProfileImage()

        profileImageView.setOnClickListener {
             val intent = Intent(context, UserProfileActivity::class.java)
            startActivity(intent)
        }
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


        val genreAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, genres)
        val platformAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, platforms)

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
                    val intent = Intent(context, GameDetailsActivity::class.java)
                    intent.putExtra("GAME_ID", game.id)
                    intent.putExtra("GAME_NAME", game.name)
                    intent.putExtra("GAME_IMAGE_URL", game.image)
                    intent.putExtra("RATING", game.rating?.toString())
                    intent.putExtra("RELEASE_DATE", game.released)
                    intent.putExtra("GENRES", game.genres?.map { it.name }?.let { ArrayList(it) })
                    intent.putExtra("PLATFORMS", game.platforms?.let { ArrayList(it) })
                    startActivity(intent)
                }

                val fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in)
                recyclerView.startAnimation(fadeIn)
            }
            .addOnFailureListener { exception ->
                Log.d("Firestore", "Error getting documents: ", exception)
            }
    }

    private fun loadUserProfileImage() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
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
