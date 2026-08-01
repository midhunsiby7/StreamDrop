package com.streamdrop.app.core.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.streamdrop.app.feature.analyze.AnalyzeScreen
import com.streamdrop.app.feature.download.DownloadScreen
import com.streamdrop.app.feature.history.HistoryScreen
import com.streamdrop.app.feature.home.HomeScreen
import com.streamdrop.app.feature.settings.SettingsScreen

/**
 * StreamDrop Navigation Graph
 *
 * Defines all routes and their enter/exit animations.
 * Bottom nav screens use a fade crossfade.
 * Full-screen destinations (Analyze, Download) slide in from the right.
 */
@Composable
fun StreamDropNavGraph(
    navController: NavHostController,
) {
    NavHost(
        navController    = navController,
        startDestination = Screen.Home.route,
        enterTransition  = { fadeIn(animationSpec = tween(300)) },
        exitTransition   = { fadeOut(animationSpec = tween(200)) },
    ) {

        // ─── Home ─────────────────────────────────────────────────────────────
        composable(route = Screen.Home.route) {
            HomeScreen(
                onAnalyze = { url ->
                    navController.navigate(Screen.Analyze.createRoute(url))
                }
            )
        }

        // ─── History ──────────────────────────────────────────────────────────
        composable(route = Screen.History.route) {
            HistoryScreen(
                onOpenDownload = { downloadId ->
                    navController.navigate(Screen.Download.createRoute(downloadId))
                }
            )
        }

        // ─── Settings ─────────────────────────────────────────────────────────
        composable(route = Screen.Settings.route) {
            SettingsScreen()
        }

        // ─── Analyze (slide from right) ───────────────────────────────────────
        composable(
            route = Screen.Analyze.route,
            arguments = listOf(
                navArgument("url") {
                    type         = NavType.StringType
                    defaultValue = ""
                }
            ),
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec  = tween(350),
                ) + fadeIn(tween(350))
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it / 3 },
                    animationSpec = tween(300),
                ) + fadeOut(tween(300))
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it / 3 },
                    animationSpec  = tween(300),
                ) + fadeIn(tween(300))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(350),
                ) + fadeOut(tween(350))
            },
        ) { backStackEntry ->
            val url = backStackEntry.arguments?.getString("url") ?: ""
            AnalyzeScreen(
                url        = url,
                onBack     = { navController.popBackStack() },
                onDownload = { downloadId ->
                    navController.navigate(Screen.Download.createRoute(downloadId)) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }

        // ─── Active Download (slide from right) ───────────────────────────────
        composable(
            route = Screen.Download.route,
            arguments = listOf(
                navArgument("downloadId") { type = NavType.LongType }
            ),
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec  = tween(350),
                ) + fadeIn(tween(350))
            },
            exitTransition  = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(350),
                ) + fadeOut(tween(350))
            },
        ) { backStackEntry ->
            val downloadId = backStackEntry.arguments?.getLong("downloadId") ?: -1L
            DownloadScreen(
                downloadId = downloadId,
                onBack     = { navController.popBackStack() },
                onGoHome   = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }
    }
}
