package bruhcollective.itaysonlab.jetibox.ui.screens.library.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bruhcollective.itaysonlab.jetibox.core.models.mediahub.ContentLocator
import bruhcollective.itaysonlab.jetibox.core.models.mediahub.Gameclip
import bruhcollective.itaysonlab.jetibox.core.models.mediahub.MediaHubEntry
import bruhcollective.itaysonlab.jetibox.core.models.mediahub.MediaHubQuery
import bruhcollective.itaysonlab.jetibox.core.models.mediahub.Screenshot
import bruhcollective.itaysonlab.jetibox.core.models.titlehub.Title
import bruhcollective.itaysonlab.jetibox.core.service.MediaHubService
import bruhcollective.itaysonlab.jetibox.core.util.TimeUtils
import bruhcollective.itaysonlab.jetibox.core.xbl_bridge.XblTitleDatabase
import bruhcollective.itaysonlab.jetibox.core.xbl_bridge.XblUserController
import bruhcollective.itaysonlab.jetibox.ui.navigation.LocalNavigationWrapper
import bruhcollective.itaysonlab.jetibox.ui.navigation.NavigationWrapper
import bruhcollective.itaysonlab.jetibox.ui.shared.FullScreenError
import bruhcollective.itaysonlab.jetibox.ui.shared.FullScreenLoading
import bruhcollective.itaysonlab.jetibox.ui.shared.components.CompositeSwitch
import coil.compose.AsyncImage
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okio.ByteString.Companion.encodeUtf8
import javax.inject.Inject

@Composable
fun CapturesScreen(
    viewModel: CapturesViewModel = hiltViewModel()
) {
    val navWrapper = LocalNavigationWrapper.current

    when (val state = viewModel.state) {
        CapturesViewModel.State.Loading -> FullScreenLoading()
        is CapturesViewModel.State.Error -> FullScreenError(state.e)

        is CapturesViewModel.State.Ready -> {
            SwipeRefresh(state = rememberSwipeRefreshState(viewModel.isReloading), onRefresh = { viewModel.reload() }) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    item(span = { GridItemSpan(3) }) {
                        CapturesHeader(
                            contentFilter = viewModel.contentFilter,
                            onSetContentFilter = viewModel::setContentFilterPub
                        )
                    }

                    items(state.content, key = { it.getEntryId() }) { item ->
                        CaptureEntry(item, onClick = { viewModel.navigateToDetails(navWrapper, item) })
                    }
                }
            }
        }
    }
}

@HiltViewModel
class CapturesViewModel @Inject constructor(
    private val json: Json,
    private val mediaHubService: MediaHubService,
    private val xblUserController: XblUserController,
    private val xblTitleDatabase: XblTitleDatabase
) : ViewModel() {
    var state by mutableStateOf<State>(State.Loading)
        private set

    var contentFilter by mutableStateOf(ContentFilter.Everything)
        private set

    var isReloading by mutableStateOf(false)
        private set

    private var statePrototype: State.Ready? = null

    init {
        viewModelScope.launch { load() }
    }

    fun reload() {
        viewModelScope.launch {
            isReloading = true
            load()
            isReloading = false
        }
    }

    private suspend fun load() {
        state = try {
            val xuid = xblUserController.xblCurrentUserNotNull.xuid

            val gameclips =
                mediaHubService.searchGameclips(MediaHubQuery.showFromOwner(xuid, 500, 0)).values
            val screenshots =
                mediaHubService.searchScreenshots(MediaHubQuery.showFromOwner(xuid, 500, 0)).values
            val combined = gameclips + screenshots

            val titles = xblTitleDatabase.getTitles(combined.map { it.getGameId() })

            State.Ready(
                content = sort(combined),
                titles = titles
            ).also { statePrototype = it; }
        } catch (e: Exception) {
            e.printStackTrace()
            State.Error(e)
        }
    }

    private fun sort(content: List<MediaHubEntry>) =
        content.sortedByDescending { TimeUtils.msDateToUnix(it.getDate()) }

    fun setContentFilterPub(filter: ContentFilter) {
        contentFilter = filter

        state = statePrototype!!.copy(content = statePrototype!!.content.let {
            when (contentFilter) {
                ContentFilter.Everything -> it
                ContentFilter.Screenshots -> it.filterIsInstance<Screenshot>()
                ContentFilter.Gameclips -> it.filterIsInstance<Gameclip>()
            }
        })
    }

    fun navigateToDetails(navigationWrapper: NavigationWrapper, item: MediaHubEntry) {
        when (item) {
            is Gameclip -> navigationWrapper.navigate("capture/gameclip/${json.encodeToString<Gameclip>(item).encodeUtf8().base64Url()}")
            is Screenshot -> navigationWrapper.navigate("capture/screenshot/${json.encodeToString<Screenshot>(item).encodeUtf8().base64Url()}")
            else -> error("Not supported!")
        }
    }

    sealed class State {
        data class Ready(
            val content: List<MediaHubEntry>,
            val titles: Map<Long, Title>
        ) : State()

        class Error(val e: Exception) : State()

        object Loading : State()
    }

    enum class ContentFilter (val icon: ImageVector) {
        Everything(Icons.Default.SelectAll),
        Screenshots(Icons.Default.Image),
        Gameclips(Icons.Default.Videocam)
    }
}

@Composable
private fun CaptureEntry(
    item: MediaHubEntry,
    onClick: () -> Unit
) {
    Box(modifier = Modifier
        .aspectRatio(1f)
        .clip(RoundedCornerShape(8.dp))) {
        AsyncImage(
            model = item.getLocators().first { it is ContentLocator.LargeThumbnail }.uri,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onClick),
            contentScale = ContentScale.Crop,
            placeholder = ColorPainter(MaterialTheme.colorScheme.surface)
        )
    }
}

@Composable
private fun CapturesHeader(
    contentFilter: CapturesViewModel.ContentFilter,
    onSetContentFilter: (CapturesViewModel.ContentFilter) -> Unit
) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 8.dp)) {
        CompositeSwitch(position = contentFilter.ordinal, onClick = { idx -> onSetContentFilter(CapturesViewModel.ContentFilter.values()[idx]) }, items = CapturesViewModel.ContentFilter.values().map { it.icon })
    }
}