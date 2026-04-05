package com.treino.pokedexkmpca.navigation

import kotlinx.serialization.Serializable

@Serializable
object HomeRoute

@Serializable
object PokedexRoute

@Serializable
object TeamRoute

@Serializable
data class PokemonDetailRoute(val pokemonId: Int)
