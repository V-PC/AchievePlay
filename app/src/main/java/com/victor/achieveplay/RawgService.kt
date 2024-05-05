package com.victor.videogamesapi


import com.victor.achieveplay.VideogameDataResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface RawgService {
    @GET ("games?key=8e238734883546c0803369b8ac17b8c9")
    suspend fun getVideogames(@Query("search") videogameName: String): Response<VideogameDataResponse>
}
