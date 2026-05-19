package com.treino.pokedexkmpca.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemon_cache")
data class PokemonCacheEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val imageUrl: String,
    val types: String // Comma-separated types
)
