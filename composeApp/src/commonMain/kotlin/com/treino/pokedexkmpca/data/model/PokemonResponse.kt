package com.treino.pokedexkmpca.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PokemonListResponse(
    val results: List<PokemonNamedResource>
)

@Serializable
data class PokemonNamedResource(
    val name: String,
    val url: String
)

@Serializable
data class PokemonDetailResponse(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val sprites: PokemonSprites,
    val types: List<PokemonTypeSlot>,
    val stats: List<PokemonStatSlot>
)

@Serializable
data class PokemonSprites(
    @SerialName("front_default") val frontDefault: String?,
    val other: OtherSprites? = null
)

@Serializable
data class OtherSprites(
    @SerialName("official-artwork") val officialArtwork: OfficialArtwork? = null
)

@Serializable
data class OfficialArtwork(
    @SerialName("front_default") val frontDefault: String? = null
)

@Serializable
data class PokemonTypeSlot(
    val type: PokemonType
)

@Serializable
data class PokemonType(
    val name: String
)

@Serializable
data class PokemonStatSlot(
    @SerialName("base_stat") val baseStat: Int,
    val stat: PokemonStatInfo
)

@Serializable
data class PokemonStatInfo(
    val name: String
)
