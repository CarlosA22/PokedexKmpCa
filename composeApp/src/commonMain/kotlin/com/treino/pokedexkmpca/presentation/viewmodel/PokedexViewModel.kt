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

    private val _selectedType = MutableStateFlow<String?>(null)
    val selectedType = _selectedType.asStateFlow()

    private val _uiState = MutableStateFlow<PokedexUiState>(PokedexUiState.Loading)
    val uiState: StateFlow<PokedexUiState> = _uiState.asStateFlow()

    private var currentOffset = 0
    private val pageSize = 20
    private var isLastPage = false
    private val fullList = mutableListOf<Pokemon>()

    init {
        initialSyncAndLoad()
    }

    private fun initialSyncAndLoad() {
        viewModelScope.launch {
            _uiState.value = PokedexUiState.Loading
            try {
                repository.syncIfNeeded()
                loadNextPage(reset = true)
            } catch (e: Exception) {
                _uiState.value = PokedexUiState.Error(e.message ?: "Erro na sincronização")
            }
        }
    }

    fun loadNextPage(reset: Boolean = false) {
        if (_uiState.value is PokedexUiState.Loading && !reset) return
        if (isLastPage && !reset) return

        viewModelScope.launch {
            if (reset) {
                currentOffset = 0
                isLastPage = false
                fullList.clear()
            }

            val newPokemons = repository.getPokedex(
                query = _searchText.value,
                type = _selectedType.value,
                limit = pageSize,
                offset = currentOffset
            )

            if (newPokemons.isEmpty()) {
                isLastPage = true
                if (reset) _uiState.value = PokedexUiState.Success(emptyList())
            } else {
                fullList.addAll(newPokemons)
                currentOffset += pageSize
                _uiState.value = PokedexUiState.Success(fullList.toList())
            }
        }
    }

    fun onSearchTextChange(text: String) {
        _searchText.value = text
        loadNextPage(reset = true)
    }

    fun onTypeSelected(type: String?) {
        _selectedType.value = if (_selectedType.value == type) null else type
        loadNextPage(reset = true)
    }

    fun refresh() {
        initialSyncAndLoad()
    }
}

sealed interface PokedexUiState {
    object Loading : PokedexUiState
    data class Success(val pokemons: List<Pokemon>) : PokedexUiState
    data class Error(val message: String) : PokedexUiState
}
