package com.treino.pokedexkmpca.domain.repository

import com.treino.pokedexkmpca.domain.model.Pokemon

interface PokemonRepository {
    suspend fun getPokedex(): List<Pokemon>
    suspend fun getPokemonById(id: Int): Pokemon?
    suspend fun toggleFavorite(pokemonId: Int)
    suspend fun getFavoritePokemons(): List<Pokemon>
}
