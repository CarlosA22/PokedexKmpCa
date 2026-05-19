package com.treino.pokedexkmpca.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.treino.pokedexkmpca.domain.model.Pokemon
import com.treino.pokedexkmpca.presentation.viewmodel.PokedexUiState
import com.treino.pokedexkmpca.presentation.viewmodel.PokedexViewModel

@Composable
fun PokedexGridScreen(
    viewModel: PokedexViewModel,
    onPokemonClick: (Int) -> Unit,
    onTeamClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchText by viewModel.searchText.collectAsState()
    val selectedType by viewModel.selectedType.collectAsState()

    val gridState = rememberLazyGridState()
    
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItemIndex = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleItemIndex >= gridState.layoutInfo.totalItemsCount - 5
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value) {
            viewModel.loadNextPage()
        }
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).statusBarsPadding()) {
                Text(
                    "Pokédex",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { viewModel.onSearchTextChange(it) },
                    placeholder = { Text("Procurar Pokémon...") },
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)),
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFF2F2F2),
                        focusedContainerColor = Color(0xFFF2F2F2),
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent
                    ),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 4.dp)
                ) {
                    val types = listOf("grass", "poison", "fire", "water", "bug", "flying", "normal", "electric", "ground", "fairy", "fighting", "psychic", "rock", "steel", "ice", "ghost", "dragon", "dark")
                    items(types) { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = { viewModel.onTypeSelected(type) },
                            label = { Text(type.capitalizePokemonName()) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = getPokemonTypeColor(type),
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Search, contentDescription = null) },
                    label = { Text("Pokedex") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onTeamClick,
                    icon = { Icon(Icons.Default.Group, contentDescription = null) },
                    label = { Text("Meu Time") }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize().background(Color.White)) {
            when (val state = uiState) {
                is PokedexUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is PokedexUiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Button(onClick = { viewModel.refresh() }) {
                            Text("Tentar novamente")
                        }
                    }
                }
                is PokedexUiState.Success -> {
                    if (state.pokemons.isEmpty()) {
                        EmptyState(false)
                    } else {
                        LazyVerticalGrid(
                            state = gridState,
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.pokemons, key = { it.id }) { pokemon ->
                                PokemonGridItem(
                                    pokemon = pokemon,
                                    onClick = { onPokemonClick(pokemon.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState(isShowingTeam: Boolean) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (isShowingTeam) Icons.Default.Group else Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = Color.LightGray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (isShowingTeam) "Seu time está vazio!" else "Nenhum Pokémon encontrado",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PokemonGridItem(
    pokemon: Pokemon,
    onClick: () -> Unit
) {
    val backgroundColor = getPokemonTypeColor(pokemon.types.firstOrNull() ?: "normal")
    
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(16.dp),
        onClick = onClick,
        colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFF6F6F6))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = pokemon.id.formatPokemonNumber(),
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.Start)
            )
            
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(backgroundColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = pokemon.imageUrl,
                    contentDescription = pokemon.name,
                    modifier = Modifier.size(80.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = pokemon.name.capitalizePokemonName(),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )
            
            Row(
                modifier = Modifier.padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                pokemon.types.forEach { type ->
                    SmallTypeChip(type)
                }
            }
        }
    }
}

@Composable
fun SmallTypeChip(type: String) {
    val color = getPokemonTypeColor(type)
    Surface(
        color = color,
        shape = RoundedCornerShape(8.dp),
    ) {
        Text(
            text = type.capitalizePokemonName(),
            color = Color.White,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            fontSize = 10.sp
        )
    }
}
