package com.portfolio.schwiftyverse.ui.characterdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.portfolio.schwiftyverse.usecase.GetCharacterByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CharacterDetailViewModel @Inject constructor(
    private val getCharacterByIdUseCase: GetCharacterByIdUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(CharacterDetailState())
    val uiState = _uiState.asStateFlow()

    init {
        savedStateHandle.get<String>("characterId")?.toIntOrNull()?.let { id ->
            loadCharacter(id)
        }
    }

    private fun loadCharacter(id: Int) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            getCharacterByIdUseCase(id)
                .onSuccess { character ->
                    _uiState.update { it.copy(isLoading = false, character = character) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }
}