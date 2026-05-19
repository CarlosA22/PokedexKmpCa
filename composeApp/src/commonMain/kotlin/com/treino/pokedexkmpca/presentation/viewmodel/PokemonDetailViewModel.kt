package com.treino.pokedexkmpca.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.treino.pokedexkmpca.domain.model.Pokemon
import com.treino.pokedexkmpca.domain.repository.PokemonRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PokemonDetailViewModel(
    private val repository: PokemonRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PokemonDetailUiState>(PokemonDetailUiState.Loading)
    val uiState: StateFlow<PokemonDetailUiState> = _uiState.asStateFlow()

    fun loadPokemon(id: Int) {
        viewModelScope.launch {
            _uiState.value = PokemonDetailUiState.Loading
            try {
                // Requirement: Mandatory HTTP for details
                val pokemon = repository.getPokemonById(id, forceRemote = true)
                if (pokemon != null) {
                    _uiState.value = PokemonDetailUiState.Success(pokemon)
                } else {
                    _uiState.value = PokemonDetailUiState.Error("Pokémon não encontrado")
                }
            } catch (e: Exception) {
                _uiState.value = PokemonDetailUiState.Error(e.message ?: "Erro desconhecido")
            }
        }
    }

    fun toggleFavorite(pokemon: Pokemon, capturedLocation: String?) {
        viewModelScope.launch {
            repository.toggleFavorite(pokemon, capturedLocation)
            val currentState = _uiState.value
            if (currentState is PokemonDetailUiState.Success) {
                val isFav = repository.isFavorite(pokemon.id)
                val updatedPokemon = currentState.pokemon.copy(isFavorite = isFav)
                _uiState.value = PokemonDetailUiState.Success(updatedPokemon)
            }
        }
    }
}

sealed interface PokemonDetailUiState {
    object Loading : PokemonDetailUiState
    data class Success(val pokemon: Pokemon) : PokemonDetailUiState
    data class Error(val message: String) : PokemonDetailUiState
}
