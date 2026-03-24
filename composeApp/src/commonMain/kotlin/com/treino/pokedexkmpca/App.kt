package com.treino.pokedexkmpca

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.treino.pokedexkmpca.data.PokemonMock
import com.treino.pokedexkmpca.navigation.HomeRoute
import com.treino.pokedexkmpca.navigation.PokedexRoute
import com.treino.pokedexkmpca.navigation.PokemonDetailRoute
import com.treino.pokedexkmpca.ui.HomeScreen
import com.treino.pokedexkmpca.ui.PokedexGridScreen
import com.treino.pokedexkmpca.ui.PokemonDetailScreen

@Composable
@Preview
fun App() {
    MaterialTheme {
        val navController = rememberNavController()

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
                PokedexGridScreen(
                    pokemons = PokemonMock.pokedex,
                    onPokemonClick = { pokemonId ->
                        navController.navigate(PokemonDetailRoute(pokemonId))
                    },
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            composable<PokemonDetailRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<PokemonDetailRoute>()
                val pokemon = PokemonMock.findById(route.pokemonId)

                PokemonDetailScreen(
                    pokemon = pokemon,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
