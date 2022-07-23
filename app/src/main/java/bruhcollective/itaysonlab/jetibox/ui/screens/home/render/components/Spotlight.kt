package bruhcollective.itaysonlab.jetibox.ui.screens.home.render.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bruhcollective.itaysonlab.jetibox.core.models.contentbuilder.ContentBuilderDataItem
import bruhcollective.itaysonlab.jetibox.core.models.contentbuilder.ContentBuilderLayer
import bruhcollective.itaysonlab.jetibox.ui.navigation.LocalNavigationWrapper
import bruhcollective.itaysonlab.jetibox.ui.screens.home.render.LayoutStorage
import coil.compose.AsyncImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Spotlight(
    items: List<ContentBuilderDataItem>,
    storage: LayoutStorage
) {
    val pagerState = rememberPagerState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
    ) {
        HorizontalPager(count = items.size, state = pagerState) { page ->
            SpotlightItem(items[page], storage)
        }

        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .statusBarsPadding(),
            activeColor = Color.White,
            inactiveColor = Color.White.copy(alpha = 0.5f),
        )

        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .height(16.dp)
                .background(MaterialTheme.colorScheme.surface)
        ) {}
    }
}

@Composable
private fun SpotlightItem(
    item: ContentBuilderDataItem,
    storage: LayoutStorage
) {
    val navController = LocalNavigationWrapper.current

    val firstLayer = item.itemLayers.first()
    val secondLayer = item.itemLayers[1] as ContentBuilderLayer.Label

    val imageUrl = when (firstLayer) {
        is ContentBuilderLayer.Editorial -> firstLayer.data.image
        is ContentBuilderLayer.GameTitle -> storage.titles[firstLayer.data.titles.first()]?.images?.firstOrNull { it.type == "SuperHeroArt" }?.url
        else -> null
    }

    val title = when (firstLayer) {
        is ContentBuilderLayer.Editorial -> firstLayer.data.label
        is ContentBuilderLayer.GameTitle -> storage.titles[firstLayer.data.titles.first()]?.name
        is ContentBuilderLayer.Label -> firstLayer.data.label
        else -> null
    }

    val onClickUrl = when (firstLayer) {
        is ContentBuilderLayer.Editorial -> firstLayer.data.action
        is ContentBuilderLayer.GameTitle -> "game/${storage.titles[firstLayer.data.titles.first()]?.titleId}"
        else -> null
    }

    Box(Modifier.fillMaxSize()) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .clickable(enabled = !onClickUrl.isNullOrEmpty()) {
                    navController.navigate(onClickUrl!!)
                },
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black,
                        )
                    )
                )
        )

        Column(
            Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
                .padding(bottom = 12.dp)
        ) {
            Text(title.orEmpty(), color = Color.White, fontSize = 21.sp)
            Text(secondLayer.data.label, color = Color.White.copy(alpha = 0.7f))
        }
    }
}