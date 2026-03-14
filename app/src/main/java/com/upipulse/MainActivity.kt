package com.upipulse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.upipulse.domain.usecase.ObserveTrackingSettingsUseCase
import com.upipulse.ui.UpiPulseAppRoot
import com.upipulse.ui.theme.UpiPulseTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var observeTrackingSettingsUseCase: ObserveTrackingSettingsUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settings by observeTrackingSettingsUseCase().collectAsState(initial = null)
            UpiPulseTheme(appTheme = settings?.theme ?: com.upipulse.domain.model.AppTheme.SYSTEM) {
                UpiPulseAppRoot()
            }
        }
    }
}
