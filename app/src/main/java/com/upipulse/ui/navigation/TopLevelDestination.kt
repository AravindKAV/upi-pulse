package com.upipulse.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.ui.graphics.vector.ImageVector

enum class TopLevelDestination(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    DASHBOARD("dashboard", Icons.Default.Assessment, "Dashboard"),
    TIMELINE("timeline", Icons.Default.Timeline, "Timeline"),
    SETTINGS("settings", Icons.Default.Settings, "Settings")
}