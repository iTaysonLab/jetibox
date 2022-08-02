package bruhcollective.itaysonlab.jetibox.ui.screens.library.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bruhcollective.itaysonlab.jetibox.core.models.collections.CollectionItem
import bruhcollective.itaysonlab.jetibox.core.models.mediahub.ContentLocator
import bruhcollective.itaysonlab.jetibox.core.models.mediahub.MediaHubEntry
import bruhcollective.itaysonlab.jetibox.core.xbl_bridge.XblCollectionController
import bruhcollective.itaysonlab.jetibox.ui.navigation.LocalNavigationWrapper
import bruhcollective.itaysonlab.jetibox.ui.shared.FullScreenError
import bruhcollective.itaysonlab.jetibox.ui.shared.FullScreenLoading
import coil.compose.AsyncImage
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@Composable
fun GamesScreen(
    viewModel: GamesViewModel = hiltViewModel()
) {
    val navWrapper = LocalNavigationWrapper.current

    when (val state = viewModel.state) {
        is GamesViewModel.State.Error -> FullScreenError(state.e)
        GamesViewModel.State.Loading -> FullScreenLoading()
        GamesViewModel.State.Ready -> {
            SwipeRefresh(state = rememberSwipeRefreshState(viewModel.isReloading), onRefresh = { viewModel.reload() }) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    /* item(span = { GridItemSpan(3) }) {
                        CapturesHeader(
                            contentFilter = viewModel.contentFilter,
                            onSetContentFilter = viewModel::setContentFilterPub
                        )
                    } */

                    items(viewModel.items, key = { it.title.titleId }) { item ->
                        GameEntry(item.title.displayImage, onClick = {
                            navWrapper.navigate("game/${ item.title.titleId }")
                        })
                    }
                }
            }
        }
    }
}

@HiltViewModel
class GamesViewModel @Inject constructor(
    private val collectionController: XblCollectionController
) : ViewModel() {
    private var _stateProto: List<XblCollectionController.CollectionItem> = emptyList()

    val items = mutableStateListOf<XblCollectionController.CollectionItem>()

    var state by mutableStateOf<State>(State.Loading)
        private set

    var isReloading by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            load()
        }
    }

    fun reload() {
        viewModelScope.launch {
            isReloading = true
            load(refresh = true)
            isReloading = false
        }
    }

    private suspend fun load(refresh: Boolean = false) {
        state = try {
            val data = collectionController.getUserCollection(forceRefresh = refresh)

            _stateProto = data

            items.clear()
            items.addAll(_stateProto.sortedBy { it.title.name[0] })

            State.Ready
        } catch (e: Exception) {
            e.printStackTrace()
            State.Error(e)
        }
    }

    sealed class State {
        class Error(val e: Exception) : State()
        object Ready : State()
        object Loading : State()
    }
}

@Composable
private fun GameEntry(
    imageUrl: String,
    onClick: () -> Unit
) {
    Box(modifier = Modifier
        .aspectRatio(1f)
        .clip(RoundedCornerShape(8.dp))) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onClick),
            contentScale = ContentScale.Crop,
            placeholder = ColorPainter(MaterialTheme.colorScheme.surface)
        )
    }
}