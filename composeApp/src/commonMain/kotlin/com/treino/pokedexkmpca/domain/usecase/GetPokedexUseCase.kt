package com.treino.pokedexkmpca.domain.usecase

import com.treino.pokedexkmpca.domain.model.Pokemon
import com.treino.pokedexkmpca.domain.repository.PokemonRepository

class GetPokedexUseCase(private val repository: PokemonRepository) {
    suspend operator fun invoke(): List<Pokemon> {
        return repository.getPokedex()
    }
}
