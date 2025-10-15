package com.portfolio.schwiftyverse.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterDao {
    @Upsert
    suspend fun upsertAll(characters: List<CharacterEntity>)

    @Query("SELECT * FROM characters")
    fun getCharactersStream(): Flow<List<CharacterEntity>>

    @Query("SELECT COUNT(*) FROM characters")
    suspend fun getCharacterCount(): Int

    @Query("DELETE FROM characters")
    suspend fun clearAll()

    @Query("SELECT * FROM characters WHERE id = :id")
    suspend fun getCharacterById(id: Int): CharacterEntity?

    @Delete
    suspend fun deleteCharacters(characters: List<CharacterEntity>)
}