package bruhcollective.itaysonlab.jetibox.ui.shared.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay

@Composable
fun FullscreenLoadingDialog(
    onDismissRequest: () -> Unit = {},
    properties: DialogProperties = DialogProperties(
        dismissOnBackPress = false,
        dismissOnClickOutside = false
    )
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = properties
    ) {
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.extraLarge)
                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp))
                .padding(32.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(56.dp)
            )
        }
    }
}

@Composable
fun FullscreenDoneDialog(
    onDismissRequest: () -> Unit
) {
    FullscreenIconDialog(icon = Icons.Default.Done, onDismissRequest = onDismissRequest)
}

@Composable
fun FullscreenErrorDialog(
    onDismissRequest: () -> Unit
) {
    FullscreenIconDialog(icon = Icons.Default.Error, onDismissRequest = onDismissRequest)
}

@Composable
private fun FullscreenIconDialog(
    icon: ImageVector,
    onDismissRequest: () -> Unit,
    properties: DialogProperties = DialogProperties(
        dismissOnBackPress = false,
        dismissOnClickOutside = false
    )
) {
    LaunchedEffect(Unit) {
        delay(500)
        onDismissRequest()
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = properties
    ) {
        Box(
            modifier = Modifier
                .clip(MaterialTheme.shapes.extraLarge)
                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp))
                .padding(32.dp)
        ) {
            Icon(
                icon, contentDescription = null, modifier = Modifier
                    .align(Alignment.Center)
                    .size(56.dp)
            )
        }
    }
}