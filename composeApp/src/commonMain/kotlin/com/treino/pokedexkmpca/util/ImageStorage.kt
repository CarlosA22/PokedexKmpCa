package com.treino.pokedexkmpca.util

import androidx.compose.runtime.Composable

interface ImageStorage {
    suspend fun saveImage(bytes: ByteArray): String?
}

@Composable
expect fun rememberImageStorage(): ImageStorage
