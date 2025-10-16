package com.portfolio.schwiftyverse.ui.characterlist

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.portfolio.schwiftyverse.R
import com.portfolio.schwiftyverse.model.CharacterModel
import com.portfolio.schwiftyverse.ui.theme.PortalGreen
import com.portfolio.schwiftyverse.ui.theme.SchwiftyverseTheme

@Composable
fun CharacterListScreen(
    onCharacterClick: (Int) -> Unit,
    viewModel: CharacterListViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    var advancedFiltersVisible by remember { mutableStateOf(false) }
    var isSearchCharactersFocused by remember { mutableStateOf(false) }
    var isSpeciesFocused by remember { mutableStateOf(false) }
    var isTypeFocused by remember { mutableStateOf(false) }

    val gridState = rememberLazyGridState()

    val statusOptions = listOf(
        stringResource(R.string.filter_option_all) to "",
        stringResource(R.string.filter_option_alive) to "Alive",
        stringResource(R.string.filter_option_dead) to "Dead",
        stringResource(R.string.filter_option_unknown) to "unknown"
    )

    val genderOptions = listOf(
        stringResource(R.string.filter_option_all) to "",
        stringResource(R.string.filter_option_female) to "Female",
        stringResource(R.string.filter_option_male) to "Male",
        stringResource(R.string.filter_option_genderless) to "Genderless"
    )

    var previousIndex by remember(gridState) { mutableIntStateOf(gridState.firstVisibleItemIndex) }
    var previousScrollOffset by remember(gridState) { mutableIntStateOf(gridState.firstVisibleItemScrollOffset) }

    val isHeaderVisible by remember(gridState) {
        derivedStateOf {
            val isScrollingUp = if (previousIndex != gridState.firstVisibleItemIndex) {
                previousIndex > gridState.firstVisibleItemIndex
            } else {
                previousScrollOffset >= gridState.firstVisibleItemScrollOffset
            }

            previousIndex = gridState.firstVisibleItemIndex
            previousScrollOffset = gridState.firstVisibleItemScrollOffset

            gridState.firstVisibleItemIndex == 0 || isScrollingUp
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        AnimatedVisibility(visible = isHeaderVisible) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.top_banner),
                        contentDescription = "Schwiftyverse Banner",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.FillWidth
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f))
                    )
                }

                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { }),
                    singleLine = true,
                    label = {
                        Text(
                            text = stringResource(R.string.text_search_characters),
                            color = if (isSearchCharactersFocused || state.searchQuery.isNotEmpty()) PortalGreen else Color.Black
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .onFocusChanged { focusState ->
                            isSearchCharactersFocused = focusState.isFocused
                        },
                    colors = TextFieldDefaults.colors(
                        unfocusedLabelColor = PortalGreen,
                        focusedIndicatorColor = PortalGreen,
                        focusedLabelColor = PortalGreen,
                        cursorColor = PortalGreen
                    )
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { advancedFiltersVisible = !advancedFiltersVisible }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stringResource(R.string.text_advanced_filters),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = PortalGreen
                            )
                            if (state.isAnyFilterActive()) {
                                Text(
                                    text = stringResource(R.string.filter_applied),
                                    color = PortalGreen.copy(alpha = 0.7f),
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                                Text(
                                    text = stringResource(R.string.filter_reset),
                                    color = Color.Red.copy(alpha = 0.8f),
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier
                                        .padding(start = 4.dp)
                                        .clickable { viewModel.resetAllFilters() }
                                )
                            }
                        }
                        Icon(
                            imageVector = if (advancedFiltersVisible) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                            contentDescription = stringResource(R.string.text_advanced_filters),
                            tint = PortalGreen
                        )
                    }

                    AnimatedVisibility(visible = advancedFiltersVisible) {
                        AdvancedFilters(
                            statusOptions = statusOptions,
                            statusFilter = state.statusFilter,
                            onStatusFilterChanged = viewModel::onStatusFilterChanged,
                            genderOptions = genderOptions,
                            genderFilter = state.genderFilter,
                            onGenderFilterChanged = viewModel::onGenderFilterChanged,
                            speciesFilter = state.speciesFilter,
                            onSpeciesFilterChanged = viewModel::onSpeciesFilterChanged,
                            typeFilter = state.typeFilter,
                            onTypeFilterChanged = viewModel::onTypeFilterChanged,
                            isSpeciesFocused = isSpeciesFocused,
                            onSpeciesFocusChanged = { isSpeciesFocused = it },
                            isTypeFocused = isTypeFocused,
                            onTypeFocusChanged = { isTypeFocused = it }
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when {
                    state.isLoading -> CircularProgressIndicator()
                    state.noResultsFound -> Text(
                        stringResource(R.string.error_search_failed),
                        color = Color.White
                    )

                    state.error != null -> {
                        val errorId = state.error
                        if (errorId != null) {
                            ErrorStateWithMessage(
                                errorId = errorId,
                                onRetry = { viewModel.retryLoad() })
                        }
                    }

                    else -> {
                        CharacterList(
                            gridState = gridState,
                            characters = state.characters,
                            onCharacterClick = onCharacterClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ErrorStateWithMessage(
    @StringRes errorId: Int,
    onRetry: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.internet_error),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(id = errorId),
            color = Color.Red,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = PortalGreen,
                contentColor = Color.Black
            ),
            contentPadding = PaddingValues(horizontal = 32.dp, vertical = 12.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = stringResource(R.string.text_try_again),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
private fun AdvancedFilters(
    statusOptions: List<Pair<String, String>>,
    statusFilter: String,
    onStatusFilterChanged: (String) -> Unit,
    genderOptions: List<Pair<String, String>>,
    genderFilter: String,
    onGenderFilterChanged: (String) -> Unit,
    speciesFilter: String,
    onSpeciesFilterChanged: (String) -> Unit,
    typeFilter: String,
    onTypeFilterChanged: (String) -> Unit,
    isSpeciesFocused: Boolean,
    onSpeciesFocusChanged: (Boolean) -> Unit,
    isTypeFocused: Boolean,
    onTypeFocusChanged: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FilterDropdown(
                label = stringResource(R.string.filter_label_status),
                options = statusOptions,
                selectedValue = statusFilter,
                onValueChange = onStatusFilterChanged,
                modifier = Modifier.weight(1f)
            )
            FilterDropdown(
                label = stringResource(R.string.filter_label_gender),
                options = genderOptions,
                selectedValue = genderFilter,
                onValueChange = onGenderFilterChanged,
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = speciesFilter,
                onValueChange = onSpeciesFilterChanged,
                label = {
                    Text(
                        stringResource(R.string.filter_label_species),
                        color = if (isSpeciesFocused || speciesFilter.isNotEmpty()) PortalGreen else Color.Black
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .onFocusChanged { onSpeciesFocusChanged(it.isFocused) },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = PortalGreen,
                    focusedLabelColor = PortalGreen,
                    unfocusedLabelColor = Color.White,
                    cursorColor = PortalGreen
                )
            )
            OutlinedTextField(
                value = typeFilter,
                onValueChange = onTypeFilterChanged,
                label = {
                    Text(
                        stringResource(R.string.filter_label_type),
                        color = if (isTypeFocused || typeFilter.isNotEmpty()) PortalGreen else Color.Black
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .onFocusChanged { onTypeFocusChanged(it.isFocused) },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = PortalGreen,
                    unfocusedLabelColor = Color.White,
                    focusedLabelColor = PortalGreen,
                    cursorColor = PortalGreen
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDropdown(
    label: String,
    options: List<Pair<String, String>>,
    selectedValue: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { isExpanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = options.find { it.second == selectedValue }?.first ?: label,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
            modifier = Modifier.menuAnchor(),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = PortalGreen,
                focusedLabelColor = PortalGreen,
                unfocusedLabelColor = PortalGreen
            )
        )
        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }
        ) {
            options.forEach { (displayName, apiValue) ->
                DropdownMenuItem(
                    text = { Text(displayName) },
                    onClick = {
                        onValueChange(apiValue)
                        isExpanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CharacterList(
    gridState: LazyGridState,
    characters: List<CharacterModel>,
    onCharacterClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        state = gridState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items = characters, key = { character -> character.id }) { character ->
            CharacterListItem(
                character = character,
                onClick = { onCharacterClick(character.id) },
                modifier = Modifier.animateItemPlacement()
            )
        }
    }
}

@Composable
private fun CharacterListItem(
    character: CharacterModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.DarkGray.copy(alpha = 0.3f)
        ),
        border = BorderStroke(1.dp, PortalGreen)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(12.dp)
                .fillMaxSize()
        ) {
            AsyncImage(
                model = character.imageUrl,
                contentDescription = character.name,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = character.name,
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color.White
            )
            Text(
                text = character.species,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = Color.LightGray
            )
        }
    }
}

@Preview(name = "Screen - Loading State", showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun CharacterListScreenLoadingPreview() {
    SchwiftyverseTheme {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

@Preview(name = "Screen - Success State", showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun CharacterListScreenSuccessPreview() {
    val fakeCharacters = listOf(
        CharacterModel(
            1,
            "Rick Sanchez",
            "Alive",
            "Human",
            "",
            "Male",
            "Earth C-137",
            "Citadel of Ricks",
            "Human"
        ),
        CharacterModel(
            2,
            "Morty Smith",
            "Alive",
            "Human",
            "",
            "Male",
            "Earth C-137",
            "Citadel of Ricks",
            "Human"
        ),
        CharacterModel(
            3,
            "Summer Smith",
            "Alive",
            "Human",
            "",
            "Female",
            "Earth C-137",
            "Citadel of Ricks",
            "Human"
        )
    )

    SchwiftyverseTheme {
        CharacterList(
            gridState = rememberLazyGridState(),
            characters = fakeCharacters,
            onCharacterClick = {}
        )
    }
}

@Preview(name = "Screen - Error State", showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun CharacterListScreenErrorPreview() {
    SchwiftyverseTheme {
        ErrorStateWithMessage(
            errorId = R.string.error_timeout_schwifty,
            onRetry = {}
        )
    }
}

@Preview(name = "Screen - No Results State", showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun CharacterListScreenNoResultsPreview() {
    SchwiftyverseTheme {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = stringResource(R.string.error_search_failed),
                color = Color.White
            )
        }
    }
}