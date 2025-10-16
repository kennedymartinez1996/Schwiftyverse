package com.portfolio.schwiftyverse

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.portfolio.schwiftyverse.local.AppDatabase
import com.portfolio.schwiftyverse.local.CharacterDao
import com.portfolio.schwiftyverse.local.CharacterEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.system.measureTimeMillis

@RunWith(AndroidJUnit4::class)
class CharacterDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: CharacterDao

    @Before
    fun setupDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        dao = database.characterDao()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun upsertAll_and_getCharactersStream_returnsCorrectData() = runTest {
        val testEntities = listOf(
            createTestCharacterEntity(id = 1, name = "Pickle Rick"),
            createTestCharacterEntity(id = 2, name = "Mr. Meeseeks")
        )

        dao.upsertAll(testEntities)

        val charactersFromDb = dao.getCharactersStream().first()
        assertEquals(2, charactersFromDb.size)
        assertEquals("Pickle Rick", charactersFromDb.find { it.id == 1 }?.name)
    }

    @Test
    fun clearAll_deletesAllCharactersFromDatabase() = runTest {
        // Arrange: Insertamos un personaje para asegurarnos de que la tabla no está vacía.
        dao.upsertAll(listOf(createTestCharacterEntity(id = 1, name = "Birdperson")))
        assertEquals(1, dao.getCharacterCount()) // Verificación intermedia

        dao.clearAll()

        val charactersFromDb = dao.getCharactersStream().first()
        assertTrue(charactersFromDb.isEmpty())
    }

    @Test
    fun upsert_whenCharacterExists_updatesExistingCharacter() = runTest {
        val originalCharacter =
            createTestCharacterEntity(id = 1, name = "Simple Rick", status = "Dead")
        dao.upsertAll(listOf(originalCharacter))

        val updatedCharacter =
            createTestCharacterEntity(id = 1, name = "Cop Rick", status = "Alive")
        dao.upsertAll(listOf(updatedCharacter))

        val characterFromDb = dao.getCharacterById(1)
        assertEquals("Cop Rick", characterFromDb?.name)
        assertEquals("Alive", characterFromDb?.status)
        assertEquals(1, dao.getCharacterCount())
    }

    @Test
    fun deleteCharacters_removesOnlySpecifiedCharacters() = runTest {
        val rick = createTestCharacterEntity(1, "Rick")
        val morty = createTestCharacterEntity(2, "Morty")
        val summer = createTestCharacterEntity(3, "Summer")
        dao.upsertAll(listOf(rick, morty, summer))

        dao.deleteCharacters(listOf(rick, summer))

        val charactersFromDb = dao.getCharactersStream().first()
        assertEquals(1, charactersFromDb.size)
        assertEquals("Morty", charactersFromDb.first().name)
    }

    @Test
    fun upsertAll_withLargeDataSet_insertsAllCharactersEfficiently() = runTest {
        val largeList = (1..1000).map { createTestCharacterEntity(id = it, name = "Clone #$it") }

        val timeInMillis = measureTimeMillis {
            dao.upsertAll(largeList)
        }
        println("--> Stress Test: Inserting 1000 characters took: $timeInMillis ms")

        val count = dao.getCharacterCount()
        assertEquals(1000, count)
    }

    private fun createTestCharacterEntity(
        id: Int,
        name: String,
        status: String = "unknown",
        species: String = "unknown",
        imageUrl: String = "",
        gender: String = "unknown",
        origin: String = "unknown",
        lastKnownLocation: String = "unknown",
        type: String = "unknown"
    ) = CharacterEntity(
        id = id,
        name = name,
        status = status,
        species = species,
        imageUrl = imageUrl,
        gender = gender,
        origin = origin,
        lastKnownLocation = lastKnownLocation,
        type = type
    )
}