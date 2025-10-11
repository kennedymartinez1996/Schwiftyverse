package com.portfolio.schwiftyverse.ui.characterlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.portfolio.schwiftyverse.model.CharacterModel

/**
 * The main screen composable that observes the ViewModel's state and delegates
 * the UI rendering to the appropriate composable.
 */
@Composable
fun CharacterListScreen(
    // Get the ViewModel instance provided by Hilt.
    viewModel: CharacterListViewModel = hiltViewModel()
) {
    // Collect the state from the ViewModel in a lifecycle-aware manner.
    // The 'by' keyword unwraps the State<T> into a T directly.
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Declarative UI: Describe what the UI should look like for each state.
        if (state.isLoading) {
            CircularProgressIndicator()
        } else if (state.error != null) {
            Text(text = stringResource(id = state.error!!))
        } else {
            CharacterList(characters = state.characters)
        }
    }
}

/**
 * Displays the list of characters using a LazyColumn.
 */
@Composable
private fun CharacterList(
    characters: List<CharacterModel>,
    modifier: Modifier = Modifier
) {
    // LazyColumn is Compose's equivalent of RecyclerView. It's highly efficient.
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(characters) { character ->
            CharacterListItem(character = character)
        }
    }
}

/**
 * Displays a single character item in the list.
 */
@Composable
private fun CharacterListItem(
    character: CharacterModel,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // AsyncImage from the Coil library loads the image from the URL.
        // It automatically handles caching (memory and disk) and placeholder/error states.
        AsyncImage(
            model = character.imageUrl,
            contentDescription = character.name,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = character.name,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "${character.species} - ${character.status}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}