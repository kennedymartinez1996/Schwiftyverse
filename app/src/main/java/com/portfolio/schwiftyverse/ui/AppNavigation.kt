package com.portfolio.schwiftyverse.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.portfolio.schwiftyverse.ui.characterdetail.CharacterDetailScreen
import com.portfolio.schwiftyverse.ui.characterlist.CharacterListScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "character_list") {
        composable("character_list") {
            CharacterListScreen(
                onCharacterClick = { characterId ->
                    navController.navigate("character_detail/$characterId")
                }
            )
        }
        composable("character_detail/{characterId}") {
            CharacterDetailScreen(navController = navController)
        }
    }
}