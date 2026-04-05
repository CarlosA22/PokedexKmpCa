package com.treino.pokedexkmpca.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.treino.pokedexkmpca.domain.model.Pokemon
import com.treino.pokedexkmpca.domain.repository.PokemonRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PokedexViewModel(
    private val repository: PokemonRepository
) : ViewModel() {

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _isShowingFavorites = MutableStateFlow(false)
    val isShowingFavorites = _isShowingFavorites.asStateFlow()

    private val _allPokemons = MutableStateFlow<List<Pokemon>>(emptyList())
    private val _isLoading = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<PokedexUiState> = combine(
        _allPokemons,
        _searchText,
        _isShowingFavorites,
        _isLoading,
        _errorMessage
    ) { pokemons, query, showFavorites, loading, error ->
        when {
            loading -> PokedexUiState.Loading
            error != null -> PokedexUiState.Error(error)
            else -> {
                val filtered = pokemons.filter {
                    val matchesQuery = it.name.contains(query, ignoreCase = true) || 
                                     it.id.toString().contains(query)
                    val matchesFavorite = if (showFavorites) it.isFavorite else true
                    matchesQuery && matchesFavorite
                }
                PokedexUiState.Success(filtered)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PokedexUiState.Loading)

    init {
        loadPokedex()
    }

    fun loadPokedex() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                _allPokemons.value = repository.getPokedex()
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Erro desconhecido"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    fun toggleFavoritesView(show: Boolean) {
        _isShowingFavorites.value = show
    }

    fun toggleFavorite(pokemonId: Int) {
        viewModelScope.launch {
            repository.toggleFavorite(pokemonId)
            // Refresh local state
            _allPokemons.value = _allPokemons.value.map {
                if (it.id == pokemonId) it.copy(isFavorite = !it.isFavorite) else it
            }
        }
    }
}

sealed interface PokedexUiState {
    object Loading : PokedexUiState
    data class Success(val pokemons: List<Pokemon>) : PokedexUiState
    data class Error(val message: String) : PokedexUiState
}
