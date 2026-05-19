package com.treino.pokedexkmpca

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.treino.pokedexkmpca.data.remote.PokeApi
import com.treino.pokedexkmpca.data.repository.PokemonRepositoryImpl
import com.treino.pokedexkmpca.domain.usecase.GetPokedexUseCase
import com.treino.pokedexkmpca.domain.usecase.GetPokemonByIdUseCase
import com.treino.pokedexkmpca.navigation.HomeRoute
import com.treino.pokedexkmpca.navigation.PokedexRoute
import com.treino.pokedexkmpca.navigation.PokemonDetailRoute
import com.treino.pokedexkmpca.navigation.TeamRoute
import com.treino.pokedexkmpca.presentation.viewmodel.PokedexUiState
import com.treino.pokedexkmpca.presentation.viewmodel.PokedexViewModel
import com.treino.pokedexkmpca.presentation.viewmodel.PokemonDetailViewModel
import com.treino.pokedexkmpca.ui.HomeScreen
import com.treino.pokedexkmpca.ui.PokedexGridScreen
import com.treino.pokedexkmpca.ui.PokemonDetailScreen
import com.treino.pokedexkmpca.ui.team.TeamScreen

@Composable
@Preview
fun App() {
    MaterialTheme {
        val navController = rememberNavController()
        
        // Manual Injection (Simplified for this assignment)
        val api = remember { PokeApi() }
        val repository = remember { PokemonRepositoryImpl(api) }
        val getPokedexUseCase = remember { GetPokedexUseCase(repository) }
        val getPokemonByIdUseCase = remember { GetPokemonByIdUseCase(repository) }

        NavHost(
            navController = navController,
            startDestination = HomeRoute,
        ) {
            composable<HomeRoute> {
                HomeScreen(
                    onSeePokedexClick = {
                        navController.navigate(PokedexRoute)
                    }
                )
            }
            composable<PokedexRoute> {
                val viewModel: PokedexViewModel = viewModel {
                    PokedexViewModel(repository)
                }

                PokedexGridScreen(
                    viewModel = viewModel,
                    onPokemonClick = { pokemonId ->
                        navController.navigate(PokemonDetailRoute(pokemonId))
                    },
                    onTeamClick = {
                        navController.navigate(TeamRoute)
                    }
                )
            }

            composable<TeamRoute> {
                val viewModel: PokedexViewModel = viewModel {
                    PokedexViewModel(repository)
                }
                val uiState by viewModel.uiState.collectAsState()
                
                val team = if (uiState is PokedexUiState.Success) {
                    (uiState as PokedexUiState.Success).pokemons.filter { it.isFavorite }
                } else {
                    emptyList()
                }

                TeamScreen(
                    team = team,
                    onPokemonClick = { id ->
                        navController.navigate(PokemonDetailRoute(id))
                    },
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            composable<PokemonDetailRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<PokemonDetailRoute>()
                val viewModel: PokemonDetailViewModel = viewModel {
                    PokemonDetailViewModel(repository)
                }
                val uiState by viewModel.uiState.collectAsState()

                LaunchedEffect(route.pokemonId) {
                    viewModel.loadPokemon(route.pokemonId)
                }

                PokemonDetailScreen(
                    uiState = uiState,
                    onTeamClick = { id ->
                        viewModel.toggleFavorite(id)
                    },
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
