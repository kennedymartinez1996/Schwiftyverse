package com.portfolio.schwiftyverse.ui.characterlist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.portfolio.schwiftyverse.model.CharacterModel
import com.portfolio.schwiftyverse.ui.theme.CardBackgroundGrayTransparent

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
    // Collect the state from the ViewModel in a lifecycle-aware manner.
    // The 'by' keyword unwraps the State<T> into a T directly.
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // 1. Show loading or error states immediately, WITHOUT animation.
        // These are placed first, directly inside the Box.
        if (state.isLoading) {
            CircularProgressIndicator()
        } else if (state.error != null) {
            Text(text = stringResource(id = state.error!!))
        }

        // 2. Animate ONLY the content that appears on success.
        // This will fade in on top of the (now empty) screen once loading is done.
        AnimatedVisibility(
            // Condition: Show this only when not loading, no error, and the list is ready.
            visible = !state.isLoading && state.error == null && state.characters.isNotEmpty(),
            // Define the fade-in animation.
            enter = fadeIn(animationSpec = tween(durationMillis = 1000))
        ) {
            // The actual list of characters.
            CharacterList(characters = state.characters, onCharacterClick = onCharacterClick)
        }
    }
}

/**
 * Displays the list of characters using a LazyColumn.
 */

@Composable
private fun CharacterList(
    characters: List<CharacterModel>,
    onCharacterClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Use LazyVerticalGrid for a two-column layout.
    LazyVerticalGrid(
        columns = GridCells.Fixed(3), // Defines 2 columns
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(characters) { character ->
            CharacterListItem(character = character, onClick = { onCharacterClick(character.id) })
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

