package com.portfolio.schwiftyverse.ui.characterdetail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.portfolio.schwiftyverse.R
import com.portfolio.schwiftyverse.ui.theme.PortalGreen

@OptIn(ExperimentalMaterial3Api::class) // Required for TopAppBar
@Composable
fun CharacterDetailScreen(
    viewModel: CharacterDetailViewModel = hiltViewModel(),
    // The screen now needs the NavController to handle the back action.
    navController: NavController
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Scaffold provides a standard layout structure (top bar, content, etc.).
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.content_description_back), color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent // Make the app bar transparent
                ),
                navigationIcon = {
                    // This is the back button.
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.content_description_back),
                            tint = PortalGreen
                        )
                    }
                }
            )
        },
        containerColor = Color.Transparent // Make the scaffold background transparent
    ) { paddingValues ->
        // The rest of your UI goes inside this Box, with the provided padding.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues), // Apply padding from the scaffold
            contentAlignment = Alignment.Center
        ) {
            if (state.isLoading) {
                CircularProgressIndicator()
            } else if (state.error != null) {
                Text(text = state.error!!, color = Color.White)
            } else if (state.character != null) {
                val character = state.character!!
                // Main content column, with vertical scrolling
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Character Image
                    AsyncImage(
                        model = character.imageUrl,
                        contentDescription = character.name,
                        modifier = Modifier
                            .size(200.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Character Name
                    Text(
                        text = character.name,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = PortalGreen
                    )

                    // Status with Icon
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Status",
                            tint = if (character.status.equals(
                                    "Alive",
                                    true
                                )
                            ) PortalGreen else Color.Red,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = character.status, color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Detail Sections
                    DetailSection(
                        title = "ORIGIN",
                        icon = Icons.Default.Public,
                        value = character.origin
                    )
                    DetailSection(
                        title = "LAST KNOWN LOCATION",
                        icon = Icons.Default.Place,
                        value = character.lastKnownLocation
                    )
                    DetailSection(
                        title = "SPECIES",
                        icon = Icons.Default.QuestionMark,
                        value = character.species
                    )
                    DetailSection(
                        title = "GENDER",
                        icon = if (character.gender == "Male") Icons.Default.Male else Icons.Default.Female,
                        value = character.gender
                    )
                }
            }
        }
    }
}


// Reusable composable for the detail sections
@Composable
private fun DetailSection(title: String, icon: ImageVector, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(text = title, color = Color.Gray, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = title, tint = PortalGreen)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = value, color = Color.White, fontSize = 16.sp)
        }
    }
}