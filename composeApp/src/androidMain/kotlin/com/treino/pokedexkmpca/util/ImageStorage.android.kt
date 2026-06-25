package com.treino.pokedexkmpca.util

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.io.File
import java.util.UUID

class AndroidImageStorage(private val context: Context) : ImageStorage {
    override suspend fun saveImage(bytes: ByteArray): String? {
        return try {
            val fileName = "pokemon_${UUID.randomUUID()}.jpg"
            val file = File(context.filesDir, fileName)
            file.writeBytes(bytes)
            file.absolutePath
        } catch (e: Exception) {
            null
        }
    }
}

@Composable
actual fun rememberImageStorage(): ImageStorage {
    val context = LocalContext.current
    return remember { AndroidImageStorage(context) }
}
