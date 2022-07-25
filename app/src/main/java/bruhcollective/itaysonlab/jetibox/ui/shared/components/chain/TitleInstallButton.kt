package bruhcollective.itaysonlab.jetibox.ui.shared.components.chain

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color

@Composable
fun TitleInstallButton (
    productIds: List<String>
) {
    var prg by remember { mutableStateOf(false) }
    val progress = animateFloatAsState(targetValue = if (prg) 1f else 0f, animationSpec = tween(durationMillis = 1000))

    Box(modifier = Modifier
        .clip(CircleShape)
        .clickable { prg = !prg }
        .background(Color.White.copy(alpha = 0.2f))
        .drawBehind {
            drawRect(
                color = Color.White.copy(alpha = 0.4f),
                topLeft = Offset(x = 0f, y = 0f),
                size = Size(size.width * progress.value, size.height)
            )
        }) {
        Row(
            Modifier.defaultMinSize(
            minWidth = ButtonDefaults.MinWidth,
            minHeight = ButtonDefaults.MinHeight
        ).padding(ButtonDefaults.ContentPadding), verticalAlignment = Alignment.CenterVertically) {
            ProvideTextStyle(value = MaterialTheme.typography.labelLarge) {
                Text("Download", color = Color.White)
            }
        }
    }
}