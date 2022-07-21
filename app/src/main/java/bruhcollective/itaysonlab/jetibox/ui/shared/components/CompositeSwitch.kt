package bruhcollective.itaysonlab.jetibox.ui.shared.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun CompositeSwitch(
    position: Int,
    onClick: (Int) -> Unit,
    items: List<ImageVector>,
) {
    // Colors
    val offset = animateDpAsState(targetValue = (44 * position).dp)

    Box(
        Modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
    ) {

        Box(
            Modifier
                .offset(x = offset.value)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .size(44.dp)
        )

        Row {
            items.forEachIndexed { index, item ->
                CompositeSwitchIcon(
                    icon = item, selected = position == index, onClick = {
                        onClick(index)
                    }
                )
            }
        }
    }
}

@Composable
private fun CompositeSwitchIcon(
    icon: ImageVector,
    onClick: () -> Unit,
    selected: Boolean
) {
    val tint =
        animateColorAsState(targetValue = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)
    Icon(
        icon, contentDescription = null, tint = tint.value, modifier = Modifier
            .clip(CircleShape)
            .clickable(enabled = !selected, onClick = onClick)
            .padding(12.dp)
            .size(20.dp)
    )
}