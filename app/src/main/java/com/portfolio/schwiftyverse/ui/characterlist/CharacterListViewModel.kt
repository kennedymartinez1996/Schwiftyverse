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

// @HiltViewModel tells Hilt that this is a ViewModel and its dependencies should be injected.
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

    private var currentPage = 1

    init {
        loadCharacters(reset = true)
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun triggerSearch() {
        loadCharacters(reset = true, query = _searchQuery.value)
    }

    fun loadCharacters(
        reset: Boolean = false,
        query: String = _searchQuery.value,
    ) {
        if (uiState.value.isLoading) return

        viewModelScope.launch {
            if (reset) {
                currentPage = 1
                _uiState.update { it.copy(isLoading = true) }
            }

            getCharactersUseCase(page = currentPage, name = query.ifEmpty { null })
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