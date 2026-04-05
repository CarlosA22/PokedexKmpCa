package com.treino.pokedexkmpca.ui.team

import androidx.compose.runtime.Composable
import com.treino.pokedexkmpca.domain.model.Pokemon

@Composable
expect fun TeamScreen(
    team: List<Pokemon>,
    onPokemonClick: (Int) -> Unit,
    onBackClick: () -> Unit
)
