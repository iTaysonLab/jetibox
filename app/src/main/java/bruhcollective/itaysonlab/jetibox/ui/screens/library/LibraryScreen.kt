package bruhcollective.itaysonlab.jetibox.ui.screens.library

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.TabPosition
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import bruhcollective.itaysonlab.jetibox.ui.screens.library.pages.CapturesScreen
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun LibraryScreen(

) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState()

    val pages = remember {
        listOf(
            LibrarySubpage(
                name = "Captures",
                content = {
                    CapturesScreen()
                }
            ),
            LibrarySubpage(
                name = "Games",
                content = {

                }
            ),
            LibrarySubpage(
                name = "Consoles",
                content = {

                }
            )
        )
    }

    Scaffold(
        topBar = {
            ScrollableTabRow(
                selectedTabIndex = pagerState.currentPage,
                modifier = Modifier.statusBarsPadding(),
                divider = {},
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier
                            .pagerTabIndicatorOffset(
                                pagerState,
                                tabPositions
                            )
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    )
                }
            ) {
                pages.forEachIndexed { index, librarySubpage ->
                    Tab(
                        text = { Text(librarySubpage.name) },
                        selected = pagerState.currentPage == index,
                        onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                    )
                }
            }
        }
    ) { padding ->
        HorizontalPager(
            count = pages.size,
            state = pagerState,
            modifier = Modifier.padding(padding)
        ) { page ->
            Surface(
                tonalElevation = 1.dp,
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .fillMaxSize(),
                content = pages[page].content
            )
        }
    }
}

private class LibrarySubpage(
    val name: String,
    val content: @Composable () -> Unit
)

//

@ExperimentalPagerApi
private fun Modifier.pagerTabIndicatorOffset(
    pagerState: PagerState,
    tabPositions: List<TabPosition>,
    pageIndexMapping: (Int) -> Int = { it },
): Modifier = layout { measurable, constraints ->
    if (tabPositions.isEmpty()) {
        // If there are no pages, nothing to show
        layout(constraints.maxWidth, 0) {}
    } else {
        val currentPage = minOf(tabPositions.lastIndex, pageIndexMapping(pagerState.currentPage))
        val currentTab = tabPositions[currentPage]
        val previousTab = tabPositions.getOrNull(currentPage - 1)
        val nextTab = tabPositions.getOrNull(currentPage + 1)
        val fraction = pagerState.currentPageOffset
        val indicatorWidth = if (fraction > 0 && nextTab != null) {
            lerp(currentTab.width, nextTab.width, fraction).roundToPx()
        } else if (fraction < 0 && previousTab != null) {
            lerp(currentTab.width, previousTab.width, -fraction).roundToPx()
        } else {
            currentTab.width.roundToPx()
        }
        val indicatorOffset = if (fraction > 0 && nextTab != null) {
            lerp(currentTab.left, nextTab.left, fraction).roundToPx()
        } else if (fraction < 0 && previousTab != null) {
            lerp(currentTab.left, previousTab.left, -fraction).roundToPx()
        } else {
            currentTab.left.roundToPx()
        }
        val placeable = measurable.measure(
            Constraints(
                minWidth = indicatorWidth,
                maxWidth = indicatorWidth,
                minHeight = 0,
                maxHeight = constraints.maxHeight
            )
        )
        layout(constraints.maxWidth, maxOf(placeable.height, constraints.minHeight)) {
            placeable.place(
                indicatorOffset,
                maxOf(constraints.minHeight - placeable.height, 0)
            )
        }
    }
}