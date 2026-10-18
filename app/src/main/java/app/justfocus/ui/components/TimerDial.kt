package app.justfocus.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import app.justfocus.ui.theme.TimerTextStyle

/** The big countdown: time in the middle, ring showing how much of the session is left. */
@Composable
fun TimerDial(timeText: String, remainingFraction: Float, modifier: Modifier = Modifier) {
    Box(modifier = modifier.size(260.dp), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            progress = { remainingFraction },
            modifier = Modifier.size(260.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            strokeWidth = 6.dp,
            strokeCap = StrokeCap.Round,
            gapSize = 0.dp,
        )
        Text(text = timeText, style = TimerTextStyle, color = MaterialTheme.colorScheme.onBackground)
    }
}
