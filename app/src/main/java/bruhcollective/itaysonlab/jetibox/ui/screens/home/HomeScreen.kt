package bruhcollective.itaysonlab.jetibox.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bruhcollective.itaysonlab.jetibox.core.models.contentbuilder.*
import bruhcollective.itaysonlab.jetibox.core.service.ContentBuilderService
import bruhcollective.itaysonlab.jetibox.core.service.TitleHubService
import bruhcollective.itaysonlab.jetibox.core.stream.extractTitlesFromCBLayout
import bruhcollective.itaysonlab.jetibox.core.xal_bridge.XalBridge
import bruhcollective.itaysonlab.jetibox.ui.LambdaNavigationController
import bruhcollective.itaysonlab.jetibox.ui.screens.home.render.HomeLayoutRender
import bruhcollective.itaysonlab.jetibox.ui.screens.home.render.LayoutStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    lambdaNavigationController: LambdaNavigationController,
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    val topBarState = rememberTopAppBarScrollState()
    val scrollBehavior = remember { TopAppBarDefaults.pinnedScrollBehavior(topBarState) }

    if (viewModel.baseLayout != null) {
        Scaffold(
            topBar = {
                bruhcollective.itaysonlab.jetibox.ui.shared.evo.SmallTopAppBar(
                    title = {},
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                            3.dp
                        )
                    ),
                    actions = {
                        IconButton(onClick = {

                        }) {
                            Icon(Icons.Default.Notifications, contentDescription = null)
                        }
                    },
                    contentPadding = PaddingValues(top = with(LocalDensity.current) {
                        WindowInsets.statusBars.getTop(LocalDensity.current).toDp()
                    }),
                    scrollBehavior = scrollBehavior
                )
            }, modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) { padding ->
            HomeLayoutRender(
                navController = lambdaNavigationController,
                data = viewModel.baseLayout!!.layout,
                storage = viewModel.storage,
                modifier = Modifier.fillMaxHeight()
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
    private val xalBridge: XalBridge,
    private val cbApi: ContentBuilderService,
    private val thService: TitleHubService
) : ViewModel() {
    var baseLayout by mutableStateOf<ContentBuilderResponse?>(null)
        private set

    val storage = LayoutStorage()

    init {
        // Start loading
        viewModelScope.launch {
            baseLayout = cbApi.getHomeLayout().also {
                // this is a mess, thanks MS
                // TODO migrate to metadata system (probably MMKV-based like Jetispot)
                storage.fillTitles(
                    thService = thService,
                    list = it.layout.extractTitlesFromCBLayout()
                )
            }
        }
    }
}