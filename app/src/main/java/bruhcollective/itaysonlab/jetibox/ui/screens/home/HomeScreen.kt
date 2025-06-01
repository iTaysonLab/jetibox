package bruhcollective.itaysonlab.jetibox.ui.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bruhcollective.itaysonlab.jetibox.core.ext.extractTitlesFromCBLayout
import bruhcollective.itaysonlab.jetibox.core.models.contentbuilder.ContentBuilderResponse
import bruhcollective.itaysonlab.jetibox.core.service.ContentBuilderService
import bruhcollective.itaysonlab.jetibox.core.xbl_bridge.XblTitleDatabase
import bruhcollective.itaysonlab.jetibox.ui.screens.home.render.HomeLayoutRender
import bruhcollective.itaysonlab.jetibox.ui.screens.home.render.LayoutStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    if (viewModel.baseLayout != null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {},
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                            3.dp
                        )
                    ),
                    scrollBehavior = scrollBehavior
                )
            }
        ) { padding ->
            HomeLayoutRender(
                data = viewModel.baseLayout!!.layout,
                storage = viewModel.storage,
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
            )
        }
    } else {
        Box(Modifier.fillMaxSize()) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(56.dp)
            )
        }
    }
}

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val cbApi: ContentBuilderService,
    private val xblTitleDatabase: XblTitleDatabase
) : ViewModel() {
    var baseLayout by mutableStateOf<ContentBuilderResponse?>(null)
        private set

    val storage = LayoutStorage()

    init {
        viewModelScope.launch {
            baseLayout = cbApi.getHomeLayout().also {
                storage.fillTitles(xblTitleDatabase = xblTitleDatabase, list = it.layout.extractTitlesFromCBLayout())
            }
        }
    }
}