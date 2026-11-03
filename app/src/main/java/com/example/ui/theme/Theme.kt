package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = DarkCrimson,
    onPrimary = Color.White,
    primaryContainer = DarkCrimsonContainer,
    onPrimaryContainer = IvoryText,
    secondary = DarkBrassGold,
    onSecondary = Color(0xFF2B0000),
    secondaryContainer = DarkBrassContainer,
    onSecondaryContainer = IvoryText,
    tertiary = DarkJade,
    onTertiary = Color(0xFF00381F),
    tertiaryContainer = DarkJadeContainer,
    onTertiaryContainer = IvoryText,
    background = ConcertHallBg,
    onBackground = IvoryText,
    surface = ConcertHallSurface,
    onSurface = IvoryText,
    surfaceVariant = ConcertHallSurfaceVariant,
    onSurfaceVariant = Color(0xFFE5DDD5),
    outline = Color(0xFF6E6259),
    outlineVariant = Color(0xFF463E39)
)

private val LightColorScheme = lightColorScheme(
    primary = ConservatoryCrimson,
    onPrimary = Color.White,
    primaryContainer = ConservatoryCrimsonContainer,
    onPrimaryContainer = EbonyInk,
    secondary = BrassGold,
    onSecondary = Color.White,
    secondaryContainer = BrassGoldContainer,
    onSecondaryContainer = EbonyInk,
    tertiary = JadeHarmony,
    onTertiary = Color.White,
    tertiaryContainer = JadeHarmonyContainer,
    onTertiaryContainer = EbonyInk,
    background = ParchmentBackground,
    onBackground = EbonyInk,
    surface = ParchmentSurface,
    onSurface = EbonyInk,
    surfaceVariant = ParchmentSurfaceVariant,
    onSurfaceVariant = WarmSlate,
    outline = Color(0xFFB8ACA0),
    outlineVariant = Color(0xFFDCD3C6)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false, // Mặc định luôn ưu tiên Giao diện Sáng (Trắng sữa & Đỏ, chữ Đen)
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
