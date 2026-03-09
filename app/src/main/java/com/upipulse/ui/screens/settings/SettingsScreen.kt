package com.upipulse.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text("Permissions")
        Text("Grant SMS, Notification Listener and Battery exclusion to keep tracking reliable.")
        Button(onClick = { /* TODO deep link to settings */ }, modifier = Modifier.padding(vertical = 12.dp)) {
            Text("Open Settings")
        }
        Text("Budgets")
        Text("Configure monthly budgets per category to unlock alerts.")
    }
}