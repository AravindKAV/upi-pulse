package com.upipulse.domain.usecase

import com.upipulse.data.preferences.UserPreferencesDataSource
import com.upipulse.domain.model.AppTheme
import javax.inject.Inject

class UpdateThemeUseCase @Inject constructor(
    private val preferences: UserPreferencesDataSource
) {
    suspend operator fun invoke(theme: AppTheme) = preferences.setTheme(theme)
}
