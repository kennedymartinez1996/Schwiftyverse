package com.portfolio.schwiftyverse.ui.characterlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.portfolio.schwiftyverse.usecase.GetCharactersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// @HiltViewModel tells Hilt that this is a ViewModel and its dependencies should be injected.
@HiltViewModel
class CharacterListViewModel @Inject constructor(
    private val getCharactersUseCase: GetCharactersUseCase
) : ViewModel() {

    // Private mutable state flow that only the ViewModel can modify.
    private val _uiState = MutableStateFlow(CharacterListState())

    // Public immutable state flow that the UI can observe.
    val uiState: StateFlow<CharacterListState> = _uiState.asStateFlow()

    // This block is executed when the ViewModel is first created.
    init {
        loadCharacters()
    }

    // Function to fetch characters from the use case.
    private fun loadCharacters() {
        // Set the state to loading.
        _uiState.update { it.copy(isLoading = true) }

        // Launch a coroutine in the ViewModel's scope.
        // This ensures the job is cancelled if the ViewModel is cleared.
        viewModelScope.launch {
            getCharactersUseCase()
                .onSuccess { characters ->
                    // On success, update the state with the character list.
                    _uiState.update {
                        it.copy(isLoading = false, characters = characters)
                    }
                }
                .onFailure { throwable ->
                    // On failure, update the state with an error message.
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = com.portfolio.schwiftyverse.R.string.error_unknown
                        )
                    }
                }
        }
    }
}