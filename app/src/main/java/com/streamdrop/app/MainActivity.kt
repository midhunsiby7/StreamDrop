package com.streamdrop.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.streamdrop.app.core.navigation.BottomNavBar
import com.streamdrop.app.core.navigation.StreamDropNavGraph
import com.streamdrop.app.core.ui.theme.StreamDropTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * MainActivity
 *
 * Single-activity architecture. Hosts the Compose UI tree:
 * - Splash Screen API for the animated splash
 * - StreamDropTheme wraps the entire app
 * - Scaffold provides bottom bar + content area
 * - StreamDropNavGraph handles all navigation
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen BEFORE super.onCreate()
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        // Keep the splash on-screen until the first frame is drawn
        // In Stage 2+ we'll also keep it visible while fetching initial data
        splashScreen.setKeepOnScreenCondition { false }

        enableEdgeToEdge()

        setContent {
            StreamDropTheme {
                StreamDropApp()
            }
        }
    }
}

@Composable
private fun StreamDropApp() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavBar(navController = navController)
        },
        containerColor = com.streamdrop.app.core.ui.theme.Background,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding()),
        ) {
            StreamDropNavGraph(navController = navController)
        }
    }
}
