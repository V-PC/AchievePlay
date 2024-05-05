package com.victor.achieveplay

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class DiscoveryActivity : AppCompatActivity() {
    private lateinit var searchView: androidx.appcompat.widget.SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var genreSpinner: android.widget.Spinner
    private lateinit var platformSpinner: android.widget.Spinner
    private lateinit var dateSpinner: android.widget.Spinner
    private lateinit var gameAdapter: GameAdapter
    private val allGames = listOf(
        // Datos ficticios para pruebas
        Game("The Legend of Zelda", "Adventure", "Nintendo", "2021"),
        Game("FIFA 22", "Sports", "Sony PlayStation", "2021"),
        Game("Final Fantasy XV", "RPG", "PC", "2019"),
        Game("Genshin Impact", "RPG", "Mobile", "2020"),
        Game("Minecraft", "Sandbox", "PC", "2011")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_discovery)

        searchView = findViewById(R.id.search_view)
        recyclerView = findViewById(R.id.recycler_view_games)
        genreSpinner = findViewById(R.id.spinner_genre)
        platformSpinner = findViewById(R.id.spinner_platform)
        dateSpinner = findViewById(R.id.spinner_date)

        // Inicializar Spinners (Filtros)
        initSpinners()

        // Configurar RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        gameAdapter = GameAdapter(allGames)
        recyclerView.adapter = gameAdapter

        // Implementar Búsqueda
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    applyFilters(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    applyFilters(it)
                }
                return false
            }
        })

        // Implementar Filtros en los Spinners
        genreSpinner.onItemSelectedListener = createFilterListener()
        platformSpinner.onItemSelectedListener = createFilterListener()
        dateSpinner.onItemSelectedListener = createFilterListener()
    }

    private fun initSpinners() {
        val genres = arrayOf("Todos", "Adventure", "Sports", "RPG", "Sandbox")
        val platforms = arrayOf("Todos", "Nintendo", "Sony PlayStation", "PC", "Mobile")
        val dates = arrayOf("Todas", "2021", "2020", "2019", "2018")

        val genreAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genres)
        val platformAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, platforms)
        val dateAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, dates)

        genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        platformAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        genreSpinner.adapter = genreAdapter
        platformSpinner.adapter = platformAdapter
        dateSpinner.adapter = dateAdapter
    }

    private fun createFilterListener(): android.widget.AdapterView.OnItemSelectedListener {
        return object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
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
    }
}
