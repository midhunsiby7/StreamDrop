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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.streamdrop.app.feature.settings.SettingsViewModel
import androidx.hilt.navigation.compose.hiltViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition { false }
        enableEdgeToEdge()

        setContent {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val isDarkTheme by settingsViewModel.isDarkTheme.collectAsState()

            StreamDropTheme(darkTheme = isDarkTheme) {
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
