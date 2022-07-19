package bruhcollective.itaysonlab.jetibox.ui.screens.store

import android.text.format.Formatter
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import bruhcollective.itaysonlab.jetibox.core.models.titlehub.*
import bruhcollective.itaysonlab.jetibox.core.service.TitleHubService
import bruhcollective.itaysonlab.jetibox.ui.navigation.LocalNavigationWrapper
import bruhcollective.itaysonlab.jetibox.ui.shared.evo.SmallTopAppBar
import coil.compose.AsyncImage
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitleStoreScreen(
    titleId: String,
    viewModel: TitleStoreScreenViewModel = hiltViewModel()
) {
    val navController = LocalNavigationWrapper.current
    val topBarState = rememberTopAppBarScrollState()
    val scrollBehavior = remember { TopAppBarDefaults.pinnedScrollBehavior(topBarState) }

    LaunchedEffect(Unit) {
        viewModel.load(titleId)
    }

    when (val data = viewModel.content) {
        is TitleStoreScreenViewModel.StoreData.Loaded -> {
            Scaffold(
                topBar = {
                    SmallTopAppBar(
                        title = {
                            TitleHeaderSmall(
                                data.app.displayImage,
                                data.app.name,
                                data.app.detail?.developerName.orEmpty(),
                                data.app.detail?.publisherName.orEmpty(),
                                Modifier.fillMaxSize().alpha(scrollBehavior.scrollFraction)
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = null)
                            }
                        },
                        colors = TopAppBarDefaults.smallTopAppBarColors(
                            containerColor = Color.Transparent,
                            navigationIconContentColor = Color.White,
                            actionIconContentColor = Color.White,
                            scrolledContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                3.dp
                            )
                        ),
                        actions = {
                            IconButton(onClick = {

                            }) {
                                Icon(Icons.Default.Share, contentDescription = null)
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
                LazyColumn {
                    item {
                        TitleHeader(
                            titleName = data.app.name,
                            titleBackground = data.app.images?.firstOrNull { it.type == "SuperHeroArt" }?.url,
                            developer = data.app.detail?.developerName.orEmpty(),
                            publisher = data.app.detail?.publisherName.orEmpty(),
                            genres = data.app.detail?.genres.orEmpty(),
                        )
                    }

                    item {
                        TitleMedia(media = data.app.images.orEmpty())
                    }

                    item {
                        Column(
                            Modifier
                                .padding(horizontal = 16.dp)
                                .padding(bottom = 16.dp)
                        ) {
                            Text(
                                text = "Description",
                                fontSize = 19.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = data.app.detail?.description.orEmpty(),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }

                    if (data.appPersonal.achievement != null) {
                        item {
                            TitleAchievementCard(data.appPersonal.achievement)
                        }
                    }

                    item {
                        TitleDevicesCard(
                            data.app.devices,
                            data.app.hardware?.maxDownloadSizeInBytes ?: 0L
                        )
                    }

                    item {
                        TitleCapabilitiesCard(data.app.detail?.attributes.orEmpty())
                    }
                }
            }
        }

        else -> {
            Box(Modifier.fillMaxSize()) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(56.dp)
                )
            }
        }
    }
}

@HiltViewModel
class TitleStoreScreenViewModel @Inject constructor(
    private val thApi: TitleHubService
) : ViewModel() {
    var content by mutableStateOf<StoreData>(StoreData.Loading)
        private set

    suspend fun load(titleId: String) {
        val app = thApi.storeBatch(
            fields = "GamePass,ContentBoard,Detail,Hardware,Image,Video,ProductId",
            body = StoreBatchRequest(titleIds = listOf(titleId))
        )

        val achievementInfo = thApi.personalTitleInfo(xuid = app.xuid, titleId = titleId)

        content = StoreData.Loaded(
            xuid = app.xuid,
            app = app.titles.first(),
            appPersonal = achievementInfo.titles.first()
        )
    }

    sealed class StoreData {
        class Loaded(
            val xuid: String,
            val app: Title,
            val appPersonal: Title,
        ) : StoreData()

        object Loading : StoreData()
    }
}

// Components

@Composable
private fun TitleHeader(
    titleName: String,
    titleBackground: String?,
    developer: String,
    publisher: String,
    genres: List<String>
) {
    val joinedGenres = remember(genres) { genres.joinToString() }

    val joinedDevs = remember(developer, publisher) {
        return@remember if (developer == publisher) {
            "$developer • $joinedGenres"
        } else {
            "$developer • $publisher"
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(360.dp)
    ) {
        AsyncImage(
            model = titleBackground,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
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
            Text(titleName, color = Color.White, fontSize = 28.sp, lineHeight = 28.sp)

            if (developer == publisher) {
                Text(joinedDevs, color = Color.White.copy(alpha = 0.7f))
            } else {
                Text(joinedDevs, color = Color.White.copy(alpha = 0.7f))
                Text(joinedGenres, color = Color.White.copy(alpha = 0.7f))
            }

            Spacer(modifier = Modifier.height(6.dp))

            Button(
                onClick = { /*TODO*/ }, colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.2f),
                    contentColor = Color.White
                )
            ) {
                Text("Download")
            }
        }

        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                .height(16.dp)
                .background(MaterialTheme.colorScheme.surface)
        ) {}
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun TitleMedia(
    media: List<TitleImage>
) {
    val screenshots = remember(media) { media.filter { it.type == "Screenshot" }.map { it.url } }
    val pagerState = rememberPagerState()

    Box(
        Modifier
            .offset(y = (-16).dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            .height(200.dp)
    ) {

        HorizontalPager(count = screenshots.size, state = pagerState) { page ->
            AsyncImage(
                model = screenshots[page],
                placeholder = ColorPainter(
                    MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
                ),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp),
            activeColor = Color.White,
            inactiveColor = Color.White.copy(alpha = 0.5f),
        )
    }
}

@Composable
private fun TitleAchievementCard(
    achSection: TitleAchievementInfo
) {
    Column(
        Modifier
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp))
            .fillMaxWidth()
            .clickable {

            }
            .padding(16.dp)) {
        Text(text = "Achievements", fontSize = 19.sp, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = (achSection.progressPercentage / 100),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(Modifier.fillMaxWidth()) {
            Text(
                text = "${achSection.currentAchievements} / ${achSection.totalAchievements}",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.align(Alignment.CenterStart)
            )
            Text(
                text = "${achSection.currentGamerscore} / ${achSection.totalGamerscore} Gamerscore",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TitleDevicesCard(
    devices: List<String>,
    size: Long
) {
    val ctx = LocalContext.current
    val formattedSize = remember(size) { "approx. ${Formatter.formatFileSize(ctx, size)}" }

    Column(
        Modifier
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp)
    ) {
        Text(text = "Available on", fontSize = 19.sp, color = MaterialTheme.colorScheme.onSurface)

        Row {
            devices.forEach { dev ->
                val data = when (dev) {
                    "PC" -> "PC" to Icons.Default.Computer
                    "XboxSeries" -> "Xbox Series X|S" to Icons.Default.Gamepad
                    "XboxOne" -> "Xbox One" to Icons.Default.Gamepad
                    else -> dev to Icons.Default.Computer
                }

                AssistChip(onClick = { /*TODO*/ }, leadingIcon = {
                    Icon(data.second, contentDescription = null)
                }, label = {
                    Text(data.first)
                })

                Spacer(modifier = Modifier.width(8.dp))
            }
        }

        Box(Modifier.fillMaxWidth()) {
            Text(
                text = "Application size",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.align(Alignment.CenterStart)
            )
            Text(
                text = formattedSize,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TitleCapabilitiesCard(
    capabilities: List<TitleAttribute>
) {
    Column(Modifier.padding(16.dp)) {
        Text(text = "Capabilities", fontSize = 19.sp, color = MaterialTheme.colorScheme.onSurface)

        FlowRow(Modifier.fillMaxWidth()) {
            capabilities.forEach { dev ->
                AssistChip(onClick = { /*TODO*/ }, label = {
                    Text(dev.name)
                })

                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

@Composable
private fun TitleHeaderSmall(
    image: String,
    name: String,
    developer: String,
    publisher: String,
    modifier: Modifier = Modifier
) {
    val joinedDevs = remember(developer, publisher) {
        return@remember if (developer == publisher) {
            "$developer"
        } else {
            "$developer • $publisher"
        }
    }

    Row(modifier) {
        AsyncImage(
            model = image,
            placeholder = ColorPainter(
                MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
            ),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .clip(RoundedCornerShape(8.dp))
                .size(42.dp),
            contentScale = ContentScale.Crop
        )

        Column(Modifier.padding(start = 16.dp).align(Alignment.CenterVertically)) {
            Text(text = name, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(text = joinedDevs, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}