package com.victor.achieveplay.data.service

import com.victor.achieveplay.data.model.VideogameDataResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query



interface RawgService {


    @GET("games?key=8e238734883546c0803369b8ac17b8c9")
    suspend fun getVideogames(
        @Query("page") page: Int,
        @Query("search") videogameName: String? = null
    ): Response<VideogameDataResponse>
}

