package com.portfolio.schwiftyverse.ui.characterlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.portfolio.schwiftyverse.R
import com.portfolio.schwiftyverse.model.CharacterModel
import com.portfolio.schwiftyverse.model.SyncState
import com.portfolio.schwiftyverse.repository.CharacterRepository
import com.portfolio.schwiftyverse.repository.SyncStatusRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

@HiltViewModel
class CharacterListViewModel @Inject constructor(
    private val characterRepository: CharacterRepository,
    private val syncStatusRepository: SyncStatusRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _statusFilter = MutableStateFlow("")
    private val _genderFilter = MutableStateFlow("")
    private val _speciesFilter = MutableStateFlow("")
    private val _typeFilter = MutableStateFlow("")

    private val _timeoutError = MutableStateFlow<Int?>(null)

    val uiState: StateFlow<CharacterListState> = combine(
        characterRepository.getCharactersStream(),
        _searchQuery,
        _statusFilter,
        _genderFilter,
        _speciesFilter,
        _typeFilter,
        _timeoutError,
        syncStatusRepository.getSyncState()
    ) { results ->

        val timeoutError = results[6] as Int?
        if (timeoutError != null) {
            return@combine CharacterListState(isLoading = false, error = timeoutError)
        }

        @Suppress("UNCHECKED_CAST")
        val charactersFromDb = results[0] as List<CharacterModel>
        val query = results[1] as String
        val status = results[2] as String
        val gender = results[3] as String
        val species = results[4] as String
        val type = results[5] as String
        val syncState = results[7] as SyncState

        val filteredList = charactersFromDb.filter { character ->
            (query.isEmpty() || character.name.contains(query, ignoreCase = true)) &&
                    (status.isEmpty() || character.status.equals(status, ignoreCase = true)) &&
                    (gender.isEmpty() || character.gender.equals(gender, ignoreCase = true)) &&
                    (species.isEmpty() || character.species.contains(species, ignoreCase = true)) &&
                    (type.isEmpty() || character.type.contains(type, ignoreCase = true))
        }

        val isAnyFilterActive =
            query.isNotEmpty() || status.isNotEmpty() || gender.isNotEmpty() || species.isNotEmpty() || type.isNotEmpty()

        val isLoading = charactersFromDb.isEmpty() && !isAnyFilterActive &&
                (syncState is SyncState.Syncing || syncState is SyncState.NotStarted)

        CharacterListState(
            isLoading = isLoading,
            characters = filteredList,
            noResultsFound = filteredList.isEmpty() && isAnyFilterActive,
            searchQuery = query,
            statusFilter = status,
            genderFilter = gender,
            speciesFilter = species,
            typeFilter = type,
            syncState = syncState
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CharacterListState(isLoading = true, syncState = SyncState.NotStarted)
    )

    init {
        viewModelScope.launch {
            characterRepository.triggerSync()
        }
        startTimeoutWatcher()
    }

    fun startTimeoutWatcher() {
        viewModelScope.launch {
            try {
                withTimeout(15_000L) {
                    uiState.first { !it.isLoading }
                }
            } catch (e: TimeoutCancellationException) {
                if (uiState.value.characters.isEmpty()) {
                    _timeoutError.value = R.string.error_timeout_schwifty
                }
            }
        }
    }

        fun retryLoad() {
        _timeoutError.value = null

        viewModelScope.launch {
            characterRepository.triggerSync()
        }

        viewModelScope.launch {
            try {
                withTimeout(5_000L) {
                    characterRepository.getCharactersStream().first { it.isNotEmpty() }
                }
            } catch (e: TimeoutCancellationException) {
                if (uiState.value.characters.isEmpty()) {
                    _timeoutError.value = R.string.error_timeout_schwifty
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onStatusFilterChanged(status: String) {
        _statusFilter.value = status
    }

    fun onGenderFilterChanged(gender: String) {
        _genderFilter.value = gender
    }

    fun onSpeciesFilterChanged(species: String) {
        _speciesFilter.value = species
    }

    fun onTypeFilterChanged(type: String) {
        _typeFilter.value = type
    }

    fun resetAllFilters() {
        _searchQuery.value = ""
        _statusFilter.value = ""
        _genderFilter.value = ""
        _speciesFilter.value = ""
        _typeFilter.value = ""
    }
}