package com.portfolio.schwiftyverse.ui.characterdetail

import com.portfolio.schwiftyverse.model.CharacterModel

data class CharacterDetailState(
    val isLoading: Boolean = false,
    val character: CharacterModel? = null,
    val error: String? = null
)