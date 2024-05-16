package com.victor.achieveplay.ui.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import com.victor.achieveplay.R
import com.victor.achieveplay.data.model.Game
import com.victor.achieveplay.ui.adapter.GameAdapter

class DiscoveryActivity : AppCompatActivity() {
    private lateinit var searchView: TextInputEditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var genreSpinner: AutoCompleteTextView
    private lateinit var platformSpinner: AutoCompleteTextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_discovery)
        val fetchGamesButton = findViewById<Button>(R.id.fetch_games_button)

        initUI()
       // initListeners()

        fetchGamesButton.setOnClickListener {
          //  FirestoreUtils.fetchAndStoreGames()
         //   fetchGames()
            val searchQuery = searchView.text.toString()
            if (searchQuery.isNotEmpty()) {
                searchGames(searchQuery)
            } else {
                Toast.makeText(this, "Por favor ingresa un término de búsqueda.", Toast.LENGTH_SHORT).show()
            }
        }
        val buttonChangeActivity = findViewById<ImageView>(R.id.btnChangeActivity)
        buttonChangeActivity.setOnClickListener {
            val intent = Intent(this, UserProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initUI(){
        searchView = findViewById(R.id.search_view)

        genreSpinner = findViewById(R.id.spinner_genre)
        platformSpinner = findViewById(R.id.spinner_platform)


        // Inicializar Spinners (Filtros)
        initSpinners()

        // Configurar RecyclerView
        recyclerView = findViewById(R.id.recycler_view_games)
        recyclerView.layoutManager = GridLayoutManager(this, 1)


    }

    private fun initSpinners() {
        val genres = arrayOf("Todos", "Adventure", "Sports", "RPG", "Sandbox")
        val platforms = arrayOf("Todas", "PC", "PlayStation 5", "Xbox One", "PlayStation4", "Xbox Series S/X", "Nintendo Switch", "iOS", "Android", "Nintendo 3DS", "PlayStation4")

        val genreAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, genres)
        val platformAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, platforms)

        genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        platformAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        genreSpinner.setAdapter(genreAdapter)
        platformSpinner.setAdapter(platformAdapter)
    }
    private fun initListeners(){
        genreSpinner.setOnItemClickListener { adapterView, view, position, id ->
            val selectedGenre = adapterView.getItemAtPosition(position).toString()
        }
        platformSpinner.setOnItemClickListener { adapterView, view, position, id ->
            val selectedPlatform = adapterView.getItemAtPosition(position).toString()
        }
    }

    /*  private fun createFilterListener(): android.widget.AdapterView.OnItemSelectedListener {
           return object : android.widget.AdapterView.OnItemSelectedListener {
               override fun onItemSelected(
                   parent: android.widget.AdapterView<*>,
                   view: android.view.View?,
                   position: Int,
                   id: Long
               ) {
                   applyFilters(searchView.query.toString())
               }

               override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
           }
       }

      private fun applyFilters(query: String) {
           val selectedGenre = genreSpinner.selectedItem.toString()
           val selectedPlatform = platformSpinner.selectedItem.toString()
           val selectedDate = dateSpinner.selectedItem.toString()

           val filteredGames = allGames.filter {
               (selectedGenre == "Todos" || it.genre == selectedGenre) &&
                       (selectedPlatform == "Todos" || it.platform == selectedPlatform) &&
                       (selectedDate == "Todas" || it.releaseDate == selectedDate) &&
                       it.name.contains(query, ignoreCase = true)
           }

           gameAdapter.updateGames(filteredGames)
       }*/

    private fun fetchGames() {
        FirebaseFirestore.getInstance().collection("Videojuegos")
            .get()
            .addOnSuccessListener { result ->
                val games = result.map { document ->
                    document.toObject(Game::class.java)
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Firebase", "Error getting documents: ", exception)
            }
    }

    fun searchGames(searchQuery: String) {
        val selectedGenre = genreSpinner.text.toString()
        val selectedPlatform = platformSpinner.text.toString()

        val db = FirebaseFirestore.getInstance()
        var query = db.collection("Videojuegos").orderBy("name").startAt(searchQuery).endAt(searchQuery + '\uf8ff')

       /* if (selectedPlatform.isNotEmpty() || !selectedPlatform.equals("Todas")) {
            query = query.whereArrayContains("platforms", selectedPlatform)
        }

        if (selectedGenre.isNotEmpty()) {
            query = query.whereEqualTo("genre", selectedGenre)
        }*/

        query.get().addOnSuccessListener { documents ->
            val games = documents.map { it.toObject(Game::class.java) }
            recyclerView.adapter = GameAdapter(games){ game->
                val intent =  Intent(this, GameDetailsActivity::class.java)
                intent.putExtra("GAME_ID", game.id)
                intent.putExtra("GAME_NAME", game.name)
                intent.putExtra("GAME_IMAGE_URL", game.image)
                intent.putExtra("RATING", game.rating)
                intent.putExtra("RELEASE_DATE", game.released)
                startActivity(intent)
            }
        }.addOnFailureListener { exception ->
            Log.d("Firestore", "Error getting documents: ", exception)
        }
    }






}

