package com.treino.pokedexkmpca.domain.repository

import com.treino.pokedexkmpca.domain.model.Pokemon
import kotlinx.coroutines.flow.Flow

interface PokemonRepository {
    suspend fun getPokedex(query: String = "", type: String? = null, limit: Int = 20, offset: Int = 0): List<Pokemon>
    suspend fun getPokemonById(id: Int, forceRemote: Boolean = false): Pokemon?
    suspend fun toggleFavorite(pokemon: Pokemon, capturedLocation: String?)
    fun getFavoritePokemons(): Flow<List<Pokemon>>
    suspend fun isFavorite(id: Int): Boolean
    suspend fun syncIfNeeded()
}
