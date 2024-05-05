package com.victor.videogamesapi

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.victor.achieveplay.VideogameDataResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object FirestoreUtils {
    const val TAG = "FirestoreUtils"
    val firestore = FirebaseFirestore.getInstance()
    const val COLLECTION_GAMES = "Videojuegos"

  /* fun fetchAndStoreGames(apiKey: String, pageSize: Int = 20, page: Int = 1) {
       RawgApi.service.getGames(apiKey, pageSize, page).enqueue(object : Callback<VideogameDataResponse> {
            override fun onResponse(call: Call<VideogameDataResponse>, response: Response<VideogameDataResponse>) {
                if (response.isSuccessful) {
                    response.body()?.videogames?.let { games ->
                        games.forEach { game ->
                            val gameData = hashMapOf(
                                "id" to game.videogameId,
                                "name" to game.name,
                                "released" to game.releasedDate,
                                "platforms" to game.platforms.map { it.platformDetails.name },
                                "rating" to game.rating,
                                "image" to game.videogameImage
                            )
                            firestore.collection(COLLECTION_GAMES).document(game.videogameId)
                                .set(gameData)
                                .addOnSuccessListener {
                                    Log.d(TAG, "Juego añadido exitosamente: ${game.name}")
                                }
                                .addOnFailureListener { e ->
                                    Log.e(TAG, "Error añadiendo el juego: ${game.name}", e)
                                }
                        }
                    }
                } else {
                    Log.e(TAG, "Error en la solicitud: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<VideogameDataResponse>, t: Throwable) {
                Log.e(TAG, "Error en la solicitud: ${t.message}", t)
            }
        })
    }*/
}
