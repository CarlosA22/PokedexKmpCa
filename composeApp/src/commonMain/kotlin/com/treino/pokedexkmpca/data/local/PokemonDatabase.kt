package com.treino.pokedexkmpca.data.local

import androidx.room.*
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.compose.runtime.Composable
import com.treino.pokedexkmpca.data.local.dao.PokemonDao
import com.treino.pokedexkmpca.data.local.entity.FavoritePokemonEntity
import com.treino.pokedexkmpca.data.local.entity.PokemonCacheEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(entities = [PokemonCacheEntity::class, FavoritePokemonEntity::class], version = 1)
@ConstructedBy(PokemonDatabaseConstructor::class)
abstract class PokemonDatabase : RoomDatabase() {
    abstract fun pokemonDao(): PokemonDao
}

@Suppress("KotlinNoActualForExpect")
expect object PokemonDatabaseConstructor : RoomDatabaseConstructor<PokemonDatabase> {
    override fun initialize(): PokemonDatabase
}

fun getRoomDatabase(
    builder: RoomDatabase.Builder<PokemonDatabase>
): PokemonDatabase {
    return builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}

@Composable
expect fun rememberDatabase(): PokemonDatabase
