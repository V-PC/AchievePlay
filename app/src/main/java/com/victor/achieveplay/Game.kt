package com.victor.achieveplay

data class Game(
    val id: String? = null,
    val name: String? = null,
    val image: String? = null,
    val rating: Double? = null,
    val released: String? = null,
    val platforms: List<String>? = null,
    val genres: List<Genre>? = null
)

