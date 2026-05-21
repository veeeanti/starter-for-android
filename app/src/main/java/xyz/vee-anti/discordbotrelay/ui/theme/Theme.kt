package xyz.vee-anti.discordbotrelay.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Discord-like dark theme
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF5865F2),
    secondary = Color(0xFF202225),
    tertiary = Color(0xFF36393f),
    background = Color(0xFF36393f),
    surface = Color(0xFF2f3136),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

private val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)

@Composable
fun AppwriteStarterKitTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        content = content,
        typography = Typography,
        colorScheme = DarkColorScheme,
    )
}
