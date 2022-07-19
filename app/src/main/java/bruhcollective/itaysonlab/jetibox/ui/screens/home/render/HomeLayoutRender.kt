package bruhcollective.itaysonlab.jetibox.ui.screens.home.render

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import bruhcollective.itaysonlab.jetibox.core.models.contentbuilder.ContentBuilderLayoutItem

@Composable
fun HomeLayoutRender(
    data: List<ContentBuilderLayoutItem>,
    storage: LayoutStorage,
    modifier: Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(
            data,
            key = { it.channelId },
            contentType = { it.channelType + it.channelStyle.orEmpty() }
        ) {
            HomeLayoutBinder(it, storage)
        }
    }
}