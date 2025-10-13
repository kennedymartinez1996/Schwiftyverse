package com.portfolio.schwiftyverse.ui.characterlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.portfolio.schwiftyverse.R
import com.portfolio.schwiftyverse.usecase.GetCharactersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@OptIn(FlowPreview::class)
@HiltViewModel
class CharacterListViewModel @Inject constructor(
    private val getCharactersUseCase: GetCharactersUseCase
) : ViewModel() {

    // Private mutable state flow that only the ViewModel can modify.
    private val _uiState = MutableStateFlow(CharacterListState())

    // Public immutable state flow that the UI can observe.
    val uiState: StateFlow<CharacterListState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _statusFilter = MutableStateFlow("")
    val statusFilter = _statusFilter.asStateFlow()

    private val _genderFilter = MutableStateFlow("")
    val genderFilter = _genderFilter.asStateFlow()

    private val _speciesFilter = MutableStateFlow("")
    val speciesFilter = _speciesFilter.asStateFlow()

    private val _typeFilter = MutableStateFlow("")
    val typeFilter = _typeFilter.asStateFlow()

    private var currentPage = 1

    init {
        loadCharacters(reset = true)
    }

    fun onStatusFilterChanged(status: String) {
        _statusFilter.value = status
    }

    fun onGenderFilterChanged(gender: String) {
        _genderFilter.value = gender
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onSpeciesFilterChanged(species: String) {
        _speciesFilter.value = species
    }

    fun onTypeFilterChanged(type: String) {
        _typeFilter.value = type
    }

    fun searchCharacters() {
        _uiState.update { it.copy(searchCounter = it.searchCounter + 1) }
        loadCharacters(reset = true)
    }

    fun resetAllFilters() {
        _statusFilter.value = ""
        _genderFilter.value = ""
        _speciesFilter.value = ""
        _typeFilter.value = ""
        _searchQuery.value = ""

        loadCharacters(reset = true)
    }

    fun loadCharacters(
        reset: Boolean = false
    ) {
        val query = _searchQuery.value
        val status = _statusFilter.value
        val gender = _genderFilter.value
        val species = _speciesFilter.value
        val type = _typeFilter.value

        if (uiState.value.isLoading) return

        viewModelScope.launch {
            if (reset) {
                currentPage = 1
                _uiState.update { it.copy(isLoading = true) }
            }

            getCharactersUseCase(
                page = currentPage,
                name = query.ifEmpty { null },
                status = status.ifEmpty { null },
                gender = gender.ifEmpty { null },
                type = type.ifEmpty { null },
                species = species.ifEmpty { null }
            )
                .onSuccess { paginatedData ->
                    val characters = paginatedData.data
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            characters = if (reset) characters else it.characters + characters,
                            canPaginate = paginatedData.info.hasNext,
                            error = null
                        )
                    }
                    if (paginatedData.info.hasNext) currentPage++
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = R.string.error_search_failed,
                            characters = emptyList()
                        )
                    }
                }
        }
    }
}