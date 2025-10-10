package com.portfolio.schwiftyverse

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.portfolio.schwiftyverse.remote.ApiService
import com.portfolio.schwiftyverse.ui.theme.SchwiftyverseTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Hilt will inject the ApiService instance.
    @Inject
    lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //NEW VERSION HAHA
        enableEdgeToEdge()
        setContent {
            SchwiftyverseTheme {
                LaunchedEffect(key1 = true) {
                    try {
                        val response = apiService.getCharacters()
                        // If successful, log the number of characters.
                        Log.d("API_SUCCESS", "Characters received: ${response.results.size}")
                    } catch (e: Exception) {
                        // If it fails, log the error.
                        Log.e("API_FAILURE", "Error: ${e.message}")
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SchwiftyverseTheme {
        Greeting("Android")
    }
}