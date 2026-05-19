package com.treino.pokedexkmpca.data.local

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

fun getDatabaseBuilder(): RoomDatabase.Builder<PokemonDatabase> {
    val dbFilePath = documentDirectory() + "/pokemon.db"
    return Room.databaseBuilder<PokemonDatabase>(
        name = dbFilePath,
    )
}

private fun documentDirectory(): String {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    return requireNotNull(documentDirectory?.path)
}

@Composable
actual fun rememberDatabase(): PokemonDatabase {
    return remember {
        getRoomDatabase(getDatabaseBuilder())
    }
}
