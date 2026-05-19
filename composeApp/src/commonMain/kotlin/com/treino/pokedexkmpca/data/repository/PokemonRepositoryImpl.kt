package com.treino.pokedexkmpca.data.repository

import com.treino.pokedexkmpca.data.local.dao.PokemonDao
import com.treino.pokedexkmpca.data.local.entity.FavoritePokemonEntity
import com.treino.pokedexkmpca.data.local.entity.PokemonCacheEntity
import com.treino.pokedexkmpca.data.remote.PokeApi
import com.treino.pokedexkmpca.domain.model.Pokemon
import com.treino.pokedexkmpca.domain.model.PokemonStat
import com.treino.pokedexkmpca.domain.repository.PokemonRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PokemonRepositoryImpl(
    private val api: PokeApi,
    private val dao: PokemonDao
) : PokemonRepository {

    override suspend fun syncIfNeeded(): Unit = coroutineScope {
        val count = dao.getCacheCount()
        if (count == 0) {
            val listResponse = api.getPokemonList(limit = 151)
            val cacheEntities = listResponse.results.map { resource ->
                async {
                    val detail = api.getPokemonDetail(resource.name)
                    PokemonCacheEntity(
                        id = detail.id,
                        name = detail.name,
                        imageUrl = detail.sprites.other?.officialArtwork?.frontDefault ?: detail.sprites.frontDefault ?: "",
                        types = detail.types.joinToString(",") { it.type.name }
                    )
                }
            }.awaitAll()
            dao.insertCache(cacheEntities)
        }
    }

    override suspend fun getPokedex(query: String, type: String?, limit: Int, offset: Int): List<Pokemon> {
        val cache = dao.getPagedFilteredCache(query, type, limit, offset)
        return cache.map { it.toDomain(dao.isFavorite(it.id)) }
    }

    override suspend fun getPokemonById(id: Int, forceRemote: Boolean): Pokemon? {
        return try {
            val detail = api.getPokemonDetail(id.toString())
            detail.toDomain(dao.isFavorite(id))
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun toggleFavorite(pokemon: Pokemon, capturedLocation: String?) {
        if (dao.isFavorite(pokemon.id)) {
            dao.deleteFavorite(pokemon.id)
        } else {
            if (capturedLocation != null) {
                dao.insertFavorite(FavoritePokemonEntity(
                    id = pokemon.id,
                    name = pokemon.name,
                    imageUrl = pokemon.imageUrl,
                    types = pokemon.types.joinToString(","),
                    capturedLocation = capturedLocation
                ))
            }
        }
    }

    override fun getFavoritePokemons(): Flow<List<Pokemon>> {
        return dao.getAllFavorites().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun isFavorite(id: Int): Boolean = dao.isFavorite(id)

    private fun PokemonCacheEntity.toDomain(isFavorite: Boolean): Pokemon {
        return Pokemon(
            id = id,
            name = name,
            imageUrl = imageUrl,
            types = types.split(","),
            height = 0,
            weight = 0,
            stats = emptyList(),
            description = "",
            isFavorite = isFavorite
        )
    }

    private fun FavoritePokemonEntity.toDomain(): Pokemon {
        return Pokemon(
            id = id,
            name = name,
            imageUrl = imageUrl,
            types = types.split(","),
            height = 0,
            weight = 0,
            stats = emptyList(),
            description = "Capturado em: $capturedLocation",
            isFavorite = true
        )
    }

    private fun com.treino.pokedexkmpca.data.model.PokemonDetailResponse.toDomain(isFavorite: Boolean): Pokemon {
        return Pokemon(
            id = this.id,
            name = this.name,
            imageUrl = this.sprites.other?.officialArtwork?.frontDefault ?: this.sprites.frontDefault ?: "",
            types = this.types.map { it.type.name },
            height = this.height,
            weight = this.weight,
            stats = this.stats.map { PokemonStat(it.stat.name, it.baseStat) },
            description = "Este Pokémon é do tipo ${this.types.joinToString(" e ") { it.type.name }}.",
            isFavorite = isFavorite
        )
    }
}
