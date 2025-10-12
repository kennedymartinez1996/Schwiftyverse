package com.portfolio.schwiftyverse.ui.characterlist

import androidx.annotation.StringRes
import com.portfolio.schwiftyverse.model.CharacterModel

// This data class represents all possible states for our character list screen.
data class CharacterListState(
    // Indicates if the data is currently being loaded.
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    // Holds the list of characters on success.
    val characters: List<CharacterModel> = emptyList(),
    // Holds an error message on failure.
    @StringRes val error: Int? = null
)