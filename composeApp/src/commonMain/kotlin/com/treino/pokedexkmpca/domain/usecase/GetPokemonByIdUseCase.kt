package com.treino.pokedexkmpca.domain.usecase

import com.treino.pokedexkmpca.domain.model.Pokemon
import com.treino.pokedexkmpca.domain.repository.PokemonRepository

class GetPokemonByIdUseCase(private val repository: PokemonRepository) {
    suspend operator fun invoke(id: Int): Pokemon? {
        return repository.getPokemonById(id)
    }
}
