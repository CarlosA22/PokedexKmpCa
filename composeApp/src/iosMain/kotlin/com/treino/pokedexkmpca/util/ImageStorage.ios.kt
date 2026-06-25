package com.treino.pokedexkmpca.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.usePinned
import platform.Foundation.*
import platform.posix.memcpy

class IosImageStorage : ImageStorage {
    @OptIn(ExperimentalForeignApi::class)
    override suspend fun saveImage(bytes: ByteArray): String? {
        val paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, true)
        val documentsDirectory = paths.first() as String
        val fileName = "pokemon_${NSUUID.UUID().UUIDString()}.jpg"
        val filePath = "$documentsDirectory/$fileName"
        
        val data = bytes.usePinned { pinned ->
            NSData.dataWithBytes(pinned.addressOf(0), bytes.size.toULong())
        }
        
        return if (data.writeToFile(filePath, true)) {
            filePath
        } else {
            null
        }
    }
}

@Composable
actual fun rememberImageStorage(): ImageStorage {
    return remember { IosImageStorage() }
}
