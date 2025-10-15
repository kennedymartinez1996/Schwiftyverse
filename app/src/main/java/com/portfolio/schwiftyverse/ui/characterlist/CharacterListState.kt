package com.portfolio.schwiftyverse.ui.characterlist

import androidx.annotation.StringRes
import com.portfolio.schwiftyverse.model.CharacterModel

data class CharacterListState(
    val isLoading: Boolean = false,

    val characters: List<CharacterModel> = emptyList(),

    val noResultsFound: Boolean = false,

    @StringRes val error: Int? = null,

    val searchQuery: String = "",
    val statusFilter: String = "",
    val genderFilter: String = "",
    val speciesFilter: String = "",
    val typeFilter: String = ""
) {
    fun isAnyFilterActive(): Boolean {
        return searchQuery.isNotEmpty() || statusFilter.isNotEmpty() || genderFilter.isNotEmpty() ||
                speciesFilter.isNotEmpty() || typeFilter.isNotEmpty()
    }
}