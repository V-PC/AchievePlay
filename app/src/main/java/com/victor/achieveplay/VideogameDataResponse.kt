package com.victor.achieveplay

import com.google.gson.annotations.SerializedName

data class VideogameDataResponse (
    @SerializedName("count") val count: Int,
    @SerializedName("results") val videogames: List<VideogameItemResponse>
)
data class VideogameItemResponse(
    @SerializedName("id") val videogameId: String,
    @SerializedName("name") val name: String,
    @SerializedName("background_image") val videogameImage: String,
    @SerializedName("released") val releasedDate: String,
    @SerializedName("platforms") val platforms: List<VideogamePlatformWrapper>,
    @SerializedName("rating") val rating : Double
)

data class VideogamePlatformWrapper(
    @SerializedName("platform") val platformDetails: VideogamePlatformDetail
)

data class VideogamePlatformDetail(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("slug") val slug: String
)
data class VideogameImageResponse(@SerializedName("url") val url:String)