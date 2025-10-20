package com.portfolio.schwiftyverse

import app.cash.turbine.test
import com.portfolio.schwiftyverse.model.CharacterModel
import com.portfolio.schwiftyverse.model.SyncState
import com.portfolio.schwiftyverse.repository.CharacterRepository
import com.portfolio.schwiftyverse.repository.SyncStatusRepository
import com.portfolio.schwiftyverse.ui.characterlist.CharacterListViewModel
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CharacterListViewModelTest {

    private lateinit var viewModel: CharacterListViewModel
    private val fakeCharacterRepository: CharacterRepository = mockk(relaxed = true)
    private val fakeSyncStatusRepository: SyncStatusRepository = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()

    private val testCharacters = listOf(
        CharacterModel(1, "Birdperson", "Alive", "Bird-Person", "", "Male", "", "", "Bird-Person"),
        CharacterModel(2, "Squanchy", "Unknown", "Cat-Person", "", "Male", "", "", "Cat-Person"),
        CharacterModel(
            3,
            "Abradolf Lincler",
            "unknown",
            "Human",
            "",
            "Male",
            "",
            "",
            "Genetic experiment"
        ),
        CharacterModel(4, "Tammy Guetermann", "Dead", "Human", "", "Female", "", "", "")
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        every { fakeCharacterRepository.getCharactersStream() } returns flowOf(testCharacters)
        val initialSyncState = MutableStateFlow<SyncState>(SyncState.NotStarted)
        every { fakeSyncStatusRepository.getSyncState() } returns initialSyncState
    }

    @Test
    fun initialState_whenViewModelStarts_showsFullCharacterList() = runTest {
        viewModel = CharacterListViewModel(fakeCharacterRepository, fakeSyncStatusRepository)

        viewModel.uiState.test {
            skipItems(1)
            val initialState = awaitItem()
            assertEquals(4, initialState.characters.size)
        }
    }

    @Test
    fun filterBySearchQuery_whenQueryMatches_showsFilteredList() = runTest {
        viewModel = CharacterListViewModel(fakeCharacterRepository, fakeSyncStatusRepository)

        viewModel.uiState.test {
            skipItems(1)
            awaitItem()

            viewModel.onSearchQueryChanged("person")
            testDispatcher.scheduler.advanceUntilIdle()

            val filteredState = awaitItem()
            assertEquals(1, filteredState.characters.size)
            assertEquals("Birdperson", filteredState.characters.first().name)
        }
    }

    @Test
    fun filterByStatus_whenDeadIsSelected_showsOnlyDeadCharacters() = runTest {
        viewModel = CharacterListViewModel(fakeCharacterRepository, fakeSyncStatusRepository)

        viewModel.uiState.test {
            skipItems(1)
            awaitItem()

            viewModel.onStatusFilterChanged("Dead")
            testDispatcher.scheduler.advanceUntilIdle()

            val filteredState = awaitItem()
            assertEquals(1, filteredState.characters.size)
            assertEquals("Tammy Guetermann", filteredState.characters.first().name)
        }
    }

    @Test
    fun whenStatusAndGenderFiltersAreCombinedTheListIsFilteredCorrectly() =
        runTest {
            viewModel = CharacterListViewModel(fakeCharacterRepository, fakeSyncStatusRepository)

            viewModel.uiState.test {
                skipItems(1)

                val initialState = awaitItem()
                assertEquals(4, initialState.characters.size)

                viewModel.onStatusFilterChanged("Unknown")
                viewModel.onGenderFilterChanged("Male")
                testDispatcher.scheduler.advanceUntilIdle()

                val filteredState = awaitItem()

                assertEquals(2, filteredState.characters.size)
                assertEquals("Squanchy", filteredState.characters.first().name)
            }
        }

    @Test
    fun filter_whenNoCharactersMatch_showsNoResultsState() = runTest {
        viewModel = CharacterListViewModel(fakeCharacterRepository, fakeSyncStatusRepository)

        viewModel.uiState.test {
            skipItems(1)
            awaitItem()

            viewModel.onSpeciesFilterChanged("Gromflomite")
            testDispatcher.scheduler.advanceUntilIdle()

            val noResultsState = awaitItem()
            assertTrue(noResultsState.characters.isEmpty())
            assertTrue(noResultsState.noResultsFound)
        }
    }

    @Test
    fun resetFilters_whenCalledAfterFiltering_restoresFullList() = runTest {
        viewModel = CharacterListViewModel(fakeCharacterRepository, fakeSyncStatusRepository)

        viewModel.uiState.test {
            skipItems(1)
            awaitItem()

            viewModel.onSearchQueryChanged("Tammy")
            testDispatcher.scheduler.advanceUntilIdle()
            val filteredState = awaitItem()
            assertEquals(1, filteredState.characters.size)

            viewModel.resetAllFilters()
            testDispatcher.scheduler.advanceUntilIdle()

            val resetState = awaitItem()
            assertEquals(4, resetState.characters.size)
            assertEquals(false, resetState.noResultsFound)
        }
    }

    @Test
    fun initialSync_whenDataNeverArrives_triggersTimeoutError() = runTest {
        runTest(testDispatcher) {
            every { fakeCharacterRepository.getCharactersStream() } returns flowOf(emptyList<CharacterModel>())

            every { fakeSyncStatusRepository.getSyncState() } returns MutableStateFlow(SyncState.NotStarted)

            viewModel = CharacterListViewModel(
                fakeCharacterRepository,
                fakeSyncStatusRepository
            )

            advanceTimeBy(15_001L)

            advanceUntilIdle()

            val finalState = viewModel.uiState.value

            assertEquals(false, finalState.isLoading)
            assertEquals(R.string.error_timeout_schwifty, finalState.error)
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when db is empty and sync is in progress, isLoading is true`() = runTest {
        every { fakeCharacterRepository.getCharactersStream() } returns flowOf(emptyList<CharacterModel>())
        every { fakeSyncStatusRepository.getSyncState() } returns MutableStateFlow(SyncState.Syncing(50))

        viewModel = CharacterListViewModel(fakeCharacterRepository, fakeSyncStatusRepository)

        viewModel.uiState.test {
            skipItems(1)
            val state = awaitItem()

            assertEquals(true, state.isLoading)
            assertTrue(state.syncState is SyncState.Syncing)
        }
    }

    @Test
    fun `when db is empty and sync has failed, isLoading is false`() = runTest {
        every { fakeCharacterRepository.getCharactersStream() } returns flowOf(emptyList<CharacterModel>())
        every { fakeSyncStatusRepository.getSyncState() } returns MutableStateFlow(SyncState.Failed)

        viewModel = CharacterListViewModel(fakeCharacterRepository, fakeSyncStatusRepository)

        viewModel.uiState.test {
            skipItems(1)
            val state = awaitItem()

            assertEquals(false, state.isLoading)
            assertTrue(state.syncState is SyncState.Failed)
        }
    }
}
