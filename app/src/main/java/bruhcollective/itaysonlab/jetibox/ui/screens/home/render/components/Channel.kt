package bruhcollective.itaysonlab.jetibox.ui.screens.home.render.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bruhcollective.itaysonlab.jetibox.core.models.contentbuilder.ContentBuilderDataItem
import bruhcollective.itaysonlab.jetibox.core.models.titlehub.Title
import bruhcollective.itaysonlab.jetibox.core.stream.extractTitlesFromCBItem
import bruhcollective.itaysonlab.jetibox.ui.LambdaNavigationController
import bruhcollective.itaysonlab.jetibox.ui.screens.home.render.LayoutStorage
import coil.compose.AsyncImage

@Composable
fun Channel(
    navController: LambdaNavigationController,
    label: String,
    items: List<ContentBuilderDataItem>,
    storage: LayoutStorage
) {
    val filtered = remember(items) { items.extractTitlesFromCBItem() }

    Column(Modifier.padding(vertical = 8.dp)) {
        Text(text = label, fontSize = 21.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 8.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filtered) { item ->
                ChannelItem(navController = navController, title = storage.titles[item]!!)
            }
        }
    }
}

@Composable
private fun ChannelItem(
    navController: LambdaNavigationController,
    title: Title
) {
    AsyncImage(model = title.displayImage, contentDescription = null, placeholder = ColorPainter(
        MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
    ), modifier = Modifier.clip(RoundedCornerShape(8.dp)).size(100.dp).clickable {
        navController.navigate("game/${title.titleId}")
    })
}