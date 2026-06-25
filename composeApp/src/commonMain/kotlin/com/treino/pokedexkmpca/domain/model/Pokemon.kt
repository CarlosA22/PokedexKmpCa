package com.treino.pokedexkmpca.domain.model

data class PokemonStat(
    val name: String,
    val value: Int
)

data class Pokemon(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val types: List<String>,
    val height: Int,
    val weight: Int,
    val stats: List<PokemonStat>,
    val description: String,
    val isFavorite: Boolean = false,
    val capturedLocation: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val photoPath: String? = null
)
