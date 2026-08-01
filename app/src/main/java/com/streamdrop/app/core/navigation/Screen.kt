package com.streamdrop.app.core.navigation

/**
 * All navigation destinations in StreamDrop.
 *
 * Using sealed class with route strings instead of a navigation enum
 * to make it easy to pass arguments in future stages (e.g. videoId for analyze).
 */
sealed class Screen(val route: String) {

    // ─── Bottom Nav Destinations ───────────────────────────────────────────────
    data object Home     : Screen("home")
    data object History  : Screen("history")
    data object Settings : Screen("settings")

    // ─── Full-screen Destinations (no bottom bar) ─────────────────────────────
    /** Analyze screen receives the URL as a query parameter */
    data object Analyze  : Screen("analyze?url={url}") {
        fun createRoute(url: String) = "analyze?url=${url}"
    }

    /** Active download screen receives the downloadId */
    data object Download : Screen("download/{downloadId}") {
        fun createRoute(downloadId: Long) = "download/$downloadId"
    }
}
