package com.victor.achieveplay.utils

import com.victor.achieveplay.data.service.RawgService
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object FirestoreUtils {
    const val TAG = "FirestoreUtils"
    val firestore = FirebaseFirestore.getInstance()
    const val COLLECTION_GAMES = "Videojuegos"
    private var retrofit = getRetrofit()


  fun fetchAndStoreGames() {
      var currentPage = 1
      val firestore = Firebase.firestore  // Initialize Firestore instance

      CoroutineScope(Dispatchers.IO).launch {
          var moreGamesAvailable = true
          while (moreGamesAvailable) {
              val response = retrofit.create(RawgService::class.java).getVideogames(currentPage)
              if (response.isSuccessful && response.body() != null) {
                  response.body()?.videogames?.let { games ->
                      Log.i("FetchAllGames", "Page: $currentPage, Games Fetched: ${games.size}")
                      currentPage++
                      games.forEach { game ->
                         // Log.i("Game", "Name: ${game.name}, Released: ${game.releasedDate}, Genres: ${game.genres}")

                          // Prepare data for Firestore
                          val gameData = hashMapOf(
                              "id" to game.videogameId,
                              "name" to game.name.lowercase(),
                              "released" to game.releasedDate,
                              "platforms" to game.platforms.map { it.platformDetails.name },
                              "rating" to game.rating,
                              "image" to game.videogameImage,
                              "genres.name" to game.genres
                          )
                          for( genre  in game.genres){
                              Log.i("Game", "Name: ${game.name}, Genre: ${genre.name}")
                          }


                          // Upload game data to Firestore
                         firestore.collection("Videojuegos").document(game.videogameId)
                              .set(gameData)
                              .addOnSuccessListener {
                                  Log.d(
                                      "FirestoreUtils",
                                      "Juego añadido exitosamente: ${game.name}"
                                  )
                              }
                              .addOnFailureListener { e ->
                                  Log.e(
                                      "FirestoreUtils",
                                      "Error añadiendo el juego: ${game.name}",
                                      e
                                  )
                              }
                      }
                      moreGamesAvailable = games.isNotEmpty()
                  } ?: run {
                      moreGamesAvailable = false
                      Log.e("FetchAllGames", "No more games to fetch or empty response")
                  }
              } else {
                  moreGamesAvailable = false
                  Log.e(
                      "FetchAllGames",
                      "Failed to fetch games or end of list reached: ${
                          response.errorBody()?.string()
                      }"
                  )
              }
          }
      }
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit
            .Builder()
            .baseUrl("https://api.rawg.io/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
