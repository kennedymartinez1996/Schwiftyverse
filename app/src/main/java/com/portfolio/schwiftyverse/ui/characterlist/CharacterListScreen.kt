package com.portfolio.schwiftyverse.ui.characterlist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.portfolio.schwiftyverse.R
import com.portfolio.schwiftyverse.model.CharacterModel
import com.portfolio.schwiftyverse.ui.theme.CardBackgroundGrayTransparent
import com.portfolio.schwiftyverse.ui.theme.PortalGreen

/**
 * The main screen composable that observes the ViewModel's state and delegates
 * the UI rendering to the appropriate composable.
 */
@Composable
fun CharacterListScreen(
    // Get the ViewModel instance provided by Hilt.
    viewModel: CharacterListViewModel = hiltViewModel(),
    onCharacterClick: (Int) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.onSearchQueryChanged(it) },
            keyboardOptions = KeyboardOptions(
                // This tells the keyboard to show a "Search" icon instead of "Enter".
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                // This defines what happens when the "Search" button is pressed.
                onSearch = { viewModel.triggerSearch() }
            ),
            singleLine = true,
            label = { Text(stringResource(R.string.text_search_characters)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = PortalGreen,
                focusedLabelColor = PortalGreen,
                cursorColor = PortalGreen
            )
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CharacterList(
                characters = state.characters,
                canPaginate = state.canPaginate,
                onCharacterClick = onCharacterClick,
                onLoadMoreCharacter = {
                    viewModel.loadCharacters(query = searchQuery)
                }
            )
            when {
                state.isLoading -> {
                    CircularProgressIndicator()
                }

                state.error != null -> {
                    Text(
                        text = stringResource(R.string.error_search_failed),
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }
            }
        }
    }
}

/**
 * Displays the list of characters using a LazyColumn.
 */

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CharacterList(
    characters: List<CharacterModel>,
    canPaginate: Boolean,
    onCharacterClick: (Int) -> Unit,
    onLoadMoreCharacter: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gridState = rememberLazyGridState()
    // Use LazyVerticalGrid for a two-column layout.
    LazyVerticalGrid(
        columns = GridCells.Fixed(3), // Defines 2 columns
        state = gridState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items = characters, key = { character -> character.id }) { character ->
            CharacterListItem(
                character = character,
                onClick = { onCharacterClick(character.id) }
            )

        }
    }
    val endOfListReached by remember {
        derivedStateOf {
            gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == gridState.layoutInfo.totalItemsCount - 1
        }
    }
    LaunchedEffect(endOfListReached) {
        if (endOfListReached && canPaginate) {
            onLoadMoreCharacter()
        }
    }
}

/**
 * Displays a single character item in the list.
 */

@Composable
private fun CharacterListItem(
    character: CharacterModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Use a Card for a nicer container with elevation and a clickable modifier.
    Card(
        modifier = modifier.clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBackgroundGrayTransparent
        )

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
                    .size(120.dp) // Make the image larger
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = character.name,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                maxLines = 1, // Ensure name doesn't wrap to multiple lines
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = character.species,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
    }
}

