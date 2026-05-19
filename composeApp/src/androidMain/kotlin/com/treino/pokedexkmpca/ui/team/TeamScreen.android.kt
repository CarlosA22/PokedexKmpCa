package com.treino.pokedexkmpca.ui.team

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.treino.pokedexkmpca.domain.model.Pokemon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun TeamScreen(
    team: List<Pokemon>,
    onPokemonClick: (Int) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meu Time (Android Material)") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        if (team.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Nenhum Pokémon no time ainda.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(team) { pokemon ->
                    ListItem(
                        headlineContent = { Text(pokemon.name) },
                        supportingContent = { Text("#${pokemon.id}") },
                        trailingContent = {
                            Button(onClick = { onPokemonClick(pokemon.id) }) {
                                Text("Ver Detalhes")
                            }
                        }
                    )
                }
            }
        }
    }
}
