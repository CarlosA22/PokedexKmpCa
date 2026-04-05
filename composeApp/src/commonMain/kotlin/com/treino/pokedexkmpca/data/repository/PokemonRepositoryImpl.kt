package com.treino.pokedexkmpca.data.repository

import com.treino.pokedexkmpca.data.remote.PokeApi
import com.treino.pokedexkmpca.domain.model.Pokemon
import com.treino.pokedexkmpca.domain.model.PokemonStat
import com.treino.pokedexkmpca.domain.repository.PokemonRepository
import kotlinx.coroutines.*

class PokemonRepositoryImpl(private val api: PokeApi) : PokemonRepository {
    
    private val favoriteIds = mutableSetOf<Int>()
    private var cachedPokedex: List<Pokemon>? = null

    override suspend fun getPokedex(): List<Pokemon> = coroutineScope {
        if (cachedPokedex != null) {
            return@coroutineScope cachedPokedex!!.map { it.copy(isFavorite = favoriteIds.contains(it.id)) }
        }
        
        val listResponse = api.getPokemonList(limit = 151)
        val pokedex = listResponse.results.map { resource ->
            async {
                api.getPokemonDetail(resource.name).toDomain()
            }
        }.awaitAll()
        
        cachedPokedex = pokedex
        pokedex.map { it.copy(isFavorite = favoriteIds.contains(it.id)) }
    }

    override suspend fun getPokemonById(id: Int): Pokemon? {
        val cached = cachedPokedex?.find { it.id == id }
        if (cached != null) return cached.copy(isFavorite = favoriteIds.contains(id))
        
        return try {
            api.getPokemonDetail(id.toString()).toDomain().copy(isFavorite = favoriteIds.contains(id))
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun toggleFavorite(pokemonId: Int) {
        if (favoriteIds.contains(pokemonId)) {
            favoriteIds.remove(pokemonId)
        } else {
            favoriteIds.add(pokemonId)
        }
    }

    override suspend fun getFavoritePokemons(): List<Pokemon> {
        val pokedex = getPokedex()
        return pokedex.filter { favoriteIds.contains(it.id) }
    }

    private fun com.treino.pokedexkmpca.data.model.PokemonDetailResponse.toDomain(): Pokemon {
        return Pokemon(
            id = this.id,
            name = this.name,
            imageUrl = this.sprites.other?.officialArtwork?.frontDefault ?: this.sprites.frontDefault ?: "",
            types = this.types.map { it.type.name },
            height = this.height,
            weight = this.weight,
            stats = this.stats.map { PokemonStat(it.stat.name, it.baseStat) },
            description = "Este Pokémon é do tipo ${this.types.joinToString(" e ") { it.type.name }}.",
            isFavorite = favoriteIds.contains(this.id)
        )
    }
}
