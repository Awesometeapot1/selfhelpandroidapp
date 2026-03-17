package com.example.selfhelp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.example.selfhelp.AppThemeOption

private val SoftLavenderScheme = lightColorScheme(
    primary          = LavenderPrimary,
    onPrimary        = LavenderOnPrimary,
    secondary        = LavenderSecondary,
    onSecondary      = LavenderOnSecondary,
    background       = LavenderBackground,
    onBackground     = LavenderOnBackground,
    surface          = LavenderSurface,
    onSurface        = LavenderOnSurface,
    surfaceVariant   = LavenderSurfaceVar,
    onSurfaceVariant = LavenderOnSurfaceVar,
)

private val ForestCalmScheme = lightColorScheme(
    primary          = ForestPrimary,
    onPrimary        = ForestOnPrimary,
    secondary        = ForestSecondary,
    onSecondary      = ForestOnSecondary,
    background       = ForestBackground,
    onBackground     = ForestOnBackground,
    surface          = ForestSurface,
    onSurface        = ForestOnSurface,
    surfaceVariant   = ForestSurfaceVar,
    onSurfaceVariant = ForestOnSurfaceVar,
)

private val DarkNightScheme = darkColorScheme(
    primary          = NightPrimary,
    onPrimary        = NightOnPrimary,
    secondary        = NightSecondary,
    onSecondary      = NightOnSecondary,
    background       = NightBackground,
    onBackground     = NightOnBackground,
    surface          = NightSurface,
    onSurface        = NightOnSurface,
    surfaceVariant   = NightSurfaceVar,
    onSurfaceVariant = NightOnSurfaceVar,
)

@Composable
fun SelfhelpTheme(
    theme: AppThemeOption = AppThemeOption.SOFT_LAVENDER,
    content: @Composable () -> Unit
) {
    val colorScheme = when (theme) {
        AppThemeOption.SOFT_LAVENDER -> SoftLavenderScheme
        AppThemeOption.FOREST_CALM   -> ForestCalmScheme
        AppThemeOption.DARK_NIGHT    -> DarkNightScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
