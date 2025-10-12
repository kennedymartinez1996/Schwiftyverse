package com.portfolio.schwiftyverse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.portfolio.schwiftyverse.ui.characterlist.CharacterListScreen
import com.portfolio.schwiftyverse.ui.theme.SchwiftyverseTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var isLoading by remember { mutableStateOf(true) }

            LaunchedEffect(key1 = true) {
                isLoading = false
            }
            
            splashScreen.setKeepOnScreenCondition {
                isLoading
            }

            SchwiftyverseTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.background),
                        contentDescription = "Background",
                        modifier = Modifier.fillMaxSize(),
                        // This scales the image to fill the screen, cropping if necessary.
                        contentScale = ContentScale.Crop
                    )
                }
                if (!isLoading) {
                    CharacterListScreen()
                }
            }
        }
    }
}