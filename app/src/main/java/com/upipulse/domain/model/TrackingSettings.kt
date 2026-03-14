package com.upipulse.domain.model

enum class AppTheme {
    LIGHT, DARK, SYSTEM
}

data class TrackingSettings(
    val smsDetectionEnabled: Boolean = true,
    val notificationDetectionEnabled: Boolean = true,
    val onboardingComplete: Boolean = false,
    val sampleDataSeeded: Boolean = false,
    val theme: AppTheme = AppTheme.SYSTEM,
    val lockEnabled: Boolean = false
)
