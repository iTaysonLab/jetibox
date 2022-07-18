package bruhcollective.itaysonlab.jetibox.ui.screens.home.render

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import bruhcollective.itaysonlab.jetibox.core.models.contentbuilder.ContentBuilderLayoutItem
import bruhcollective.itaysonlab.jetibox.ui.LambdaNavigationController
import bruhcollective.itaysonlab.jetibox.ui.screens.home.render.components.Spotlight

@Composable
fun HomeLayoutBinder(
    navController: LambdaNavigationController,
    item: ContentBuilderLayoutItem,
    storage: LayoutStorage
) {
    when {
        item.channelId == "Spotlight" -> {
            Spotlight(navController, item.channelData?.items.orEmpty(), storage)
        }

        else -> Text("[unknown] ${item.channelType}")
    }
}