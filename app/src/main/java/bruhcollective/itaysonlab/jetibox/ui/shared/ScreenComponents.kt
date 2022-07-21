package bruhcollective.itaysonlab.jetibox.ui.shared

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FullScreenLoading() {
    Box(Modifier.fillMaxSize()) {
        CircularProgressIndicator(modifier = Modifier
            .align(Alignment.Center)
            .size(56.dp))
    }
}

@Composable
fun FullScreenError(e: Exception) {
    Box(Modifier.fillMaxSize()) {
        Column(modifier = Modifier
            .align(Alignment.Center)
            .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Error, contentDescription = null, modifier = Modifier.size(56.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "An error has occurred while loading the page.")
        }
    }
}