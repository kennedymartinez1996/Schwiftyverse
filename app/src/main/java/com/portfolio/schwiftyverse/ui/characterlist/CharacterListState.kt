package com.portfolio.schwiftyverse.ui.characterlist

import androidx.annotation.StringRes
import com.portfolio.schwiftyverse.model.CharacterModel
import com.portfolio.schwiftyverse.model.InfoModel

// This data class represents all possible states for our character list screen.
data class CharacterListState(
    val isLoading: Boolean = false,
    val characters: List<CharacterModel> = emptyList(),
    val info: InfoModel? = null,
    val canPaginate: Boolean = false,
    @StringRes val error: Int? = null
)