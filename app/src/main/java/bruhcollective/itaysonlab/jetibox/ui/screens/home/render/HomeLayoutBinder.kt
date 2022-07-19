package bruhcollective.itaysonlab.jetibox.ui.screens.home.render

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import bruhcollective.itaysonlab.jetibox.core.models.contentbuilder.ContentBuilderLayoutItem
import bruhcollective.itaysonlab.jetibox.ui.screens.home.render.components.Channel
import bruhcollective.itaysonlab.jetibox.ui.screens.home.render.components.Spotlight

@Composable
fun HomeLayoutBinder(
    item: ContentBuilderLayoutItem,
    storage: LayoutStorage
) {
    when {
        item.channelId == "Spotlight" -> {
            Spotlight(item.channelData?.items.orEmpty(), storage)
        }

        item.channelType == "content" && item.channelStyle == "Channel" -> {
            Channel(item.channelLabel.orEmpty(), item.channelData?.items.orEmpty(), storage)
        }

        else -> Text("[unknown] ${item.channelType}")
    }
}