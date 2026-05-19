package com.treino.pokedexkmpca.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<PokemonDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath("pokemon.db")
    return Room.databaseBuilder<PokemonDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}

@Composable
actual fun rememberDatabase(): PokemonDatabase {
    val context = LocalContext.current
    return remember {
        getRoomDatabase(getDatabaseBuilder(context))
    }
}
