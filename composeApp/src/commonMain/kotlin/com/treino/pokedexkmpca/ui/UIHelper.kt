package com.treino.pokedexkmpca.ui

import androidx.compose.ui.graphics.Color

fun String.capitalizePokemonName(): String =
    replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

fun Int.formatPokemonNumber(): String = "N°${toString().padStart(3, '0')}"

fun getPokemonTypeColor(type: String): Color {
    return when (type.lowercase()) {
        "grass" -> Color(0xFF74CB48)
        "poison" -> Color(0xFFA43E9E)
        "fire" -> Color(0xFFF57D31)
        "water" -> Color(0xFF6493EB)
        "bug" -> Color(0xFFA7B723)
        "flying" -> Color(0xFFA891EC)
        "normal" -> Color(0xFFAAA67F)
        "electric" -> Color(0xFFF9CF30)
        "ground" -> Color(0xFFDEC16B)
        "fairy" -> Color(0xFFE69EAC)
        "fighting" -> Color(0xFFC12239)
        "psychic" -> Color(0xFFFB5584)
        "rock" -> Color(0xFFB69E31)
        "steel" -> Color(0xFFB7B9D0)
        "ice" -> Color(0xFF9AD6DF)
        "ghost" -> Color(0xFF70559B)
        "dragon" -> Color(0xFF7037FF)
        "dark" -> Color(0xFF75574C)
        else -> Color.Gray
    }
}
