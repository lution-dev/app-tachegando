package org.example.project.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val LightColorScheme = lightColorScheme(
    primary = AppColors.Primary,
    onPrimary = AppColors.OnPrimary,
    primaryContainer = AppColors.PrimaryLight,
    secondary = AppColors.Secondary,
    onSecondary = AppColors.OnSecondary,
    secondaryContainer = AppColors.SecondaryLight,
    tertiary = AppColors.Accent,
    background = AppColors.Background,
    onBackground = AppColors.OnBackground,
    surface = AppColors.Surface,
    onSurface = AppColors.OnSurface,
    surfaceVariant = AppColors.SurfaceVariant,
    onSurfaceVariant = AppColors.OnSurfaceVariant,
    error = AppColors.Error,
    outline = AppColors.Outline,
)

private val DarkColorScheme = darkColorScheme(
    primary = AppColors.Primary,
    onPrimary = AppColors.OnPrimary,
    primaryContainer = AppColors.PrimaryDark,
    secondary = AppColors.Secondary,
    onSecondary = AppColors.OnSecondary,
    secondaryContainer = AppColors.SecondaryDark,
    tertiary = AppColors.Accent,
    background = AppColors.DarkBackground,
    onBackground = AppColors.DarkOnBackground,
    surface = AppColors.DarkSurface,
    onSurface = AppColors.DarkOnSurface,
    surfaceVariant = AppColors.DarkSurfaceVariant,
    onSurfaceVariant = AppColors.DarkOnSurfaceVariant,
    error = AppColors.Error,
    outline = AppColors.DarkOutline,
)

private val AppTypography = Typography(
    displayLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
    ),
    displayMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
    ),
    headlineLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 22.sp,
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    bodySmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    labelMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    ),
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content,
    )
}
