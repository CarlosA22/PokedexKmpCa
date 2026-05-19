package com.treino.pokedexkmpca.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.treino.pokedexkmpca.data.local.entity.FavoritePokemonEntity
import com.treino.pokedexkmpca.data.local.entity.PokemonCacheEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PokemonDao {
    // Cache
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCache(pokemons: List<PokemonCacheEntity>)

    @Query("""
        SELECT * FROM pokemon_cache 
        WHERE (name LIKE '%' || :query || '%' OR CAST(id AS TEXT) LIKE '%' || :query || '%') 
        AND (:typeFilter IS NULL OR types LIKE '%' || :typeFilter || '%') 
        ORDER BY id ASC 
        LIMIT :limit OFFSET :offset
    """)
    suspend fun getPagedFilteredCache(query: String, typeFilter: String?, limit: Int, offset: Int): List<PokemonCacheEntity>

    @Query("SELECT COUNT(*) FROM pokemon_cache")
    suspend fun getCacheCount(): Int

    // Favorites
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(pokemon: FavoritePokemonEntity)

    @Query("DELETE FROM favorite_pokemons WHERE id = :id")
    suspend fun deleteFavorite(id: Int)

    @Query("SELECT * FROM favorite_pokemons ORDER BY id ASC")
    fun getAllFavorites(): Flow<List<FavoritePokemonEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_pokemons WHERE id = :id)")
    suspend fun isFavorite(id: Int): Boolean

    @Query("SELECT * FROM favorite_pokemons WHERE id = :id")
    suspend fun getFavoriteById(id: Int): FavoritePokemonEntity?
}
