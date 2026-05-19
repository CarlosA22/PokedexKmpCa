package com.treino.pokedexkmpca.data.remote

import com.treino.pokedexkmpca.data.model.PokemonDetailResponse
import com.treino.pokedexkmpca.data.model.PokemonListResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class PokeApi {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            })
        }
    }

    suspend fun getPokemonList(limit: Int = 20, offset: Int = 0): PokemonListResponse {
        return client.get("https://pokeapi.co/api/v2/pokemon?limit=$limit\u0026offset=$offset").body()
    }

    suspend fun getPokemonDetail(idOrName: String): PokemonDetailResponse {
        return client.get("https://pokeapi.co/api/v2/pokemon/$idOrName").body()
    }

    suspend fun getTypeList(): PokemonListResponse {
        return client.get("https://pokeapi.co/api/v2/type").body()
    }
}
