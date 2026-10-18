package app.justfocus.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val Purple = Color(0xFFA78BFA)
private val PurpleDeep = Color(0xFF2E1F4F)
private val Night = Color(0xFF0D0B12)
private val NightRaised = Color(0xFF17131F)
private val NightOutline = Color(0xFF2A2435)
private val TextPrimary = Color(0xFFECE8F4)
private val TextMuted = Color(0xFF948DA3)

private val JustFocusColors = darkColorScheme(
    primary = Purple,
    onPrimary = Color(0xFF1A1030),
    primaryContainer = PurpleDeep,
    onPrimaryContainer = TextPrimary,
    secondaryContainer = PurpleDeep,
    onSecondaryContainer = TextPrimary,
    background = Night,
    onBackground = TextPrimary,
    surface = Night,
    onSurface = TextPrimary,
    surfaceVariant = NightRaised,
    onSurfaceVariant = TextMuted,
    surfaceContainer = NightRaised,
    outline = NightOutline,
    outlineVariant = NightOutline,
    errorContainer = Color(0xFF3A1F2B),
)

/** Large, light digits for the countdown, with equal-width numbers so the clock does not jitter. */
val TimerTextStyle = TextStyle(
    fontSize = 64.sp,
    fontWeight = FontWeight.Light,
    fontFeatureSettings = "tnum",
    letterSpacing = 2.sp,
)

@Composable
fun JustFocusTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = JustFocusColors,
        typography = Typography(),
        content = content,
    )
}
