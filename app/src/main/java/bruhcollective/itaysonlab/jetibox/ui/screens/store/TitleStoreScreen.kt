package bruhcollective.itaysonlab.jetibox.ui.screens.store

import android.text.format.Formatter
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import bruhcollective.itaysonlab.jetibox.R
import bruhcollective.itaysonlab.jetibox.core.config.MsCapDatabase
import bruhcollective.itaysonlab.jetibox.core.models.titlehub.StoreBatchRequest
import bruhcollective.itaysonlab.jetibox.core.models.titlehub.Title
import bruhcollective.itaysonlab.jetibox.core.models.titlehub.TitleAchievementInfo
import bruhcollective.itaysonlab.jetibox.core.models.titlehub.TitleContentWarnings
import bruhcollective.itaysonlab.jetibox.core.models.titlehub.TitleImage
import bruhcollective.itaysonlab.jetibox.core.service.TitleHubService
import bruhcollective.itaysonlab.jetibox.core.xbl_bridge.XccsController
import bruhcollective.itaysonlab.jetibox.ui.navigation.LocalNavigationWrapper
import bruhcollective.itaysonlab.jetibox.ui.shared.FullScreenError
import bruhcollective.itaysonlab.jetibox.ui.shared.FullScreenLoading
import bruhcollective.itaysonlab.jetibox.ui.shared.components.chain.TitleInstallButton
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
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    LaunchedEffect(Unit) {
        viewModel.load(titleId)
    }

    when (val data = viewModel.content) {
        is TitleStoreScreenViewModel.StoreData.Loaded -> {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            TitleHeaderSmall(
                                data.app.displayImage,
                                data.app.name,
                                data.app.detail?.developerName.orEmpty(),
                                data.app.detail?.publisherName.orEmpty(),
                                Modifier
                                    .fillMaxSize()
                                    .alpha(scrollBehavior.state.overlappedFraction)
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = null)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
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
                        scrollBehavior = scrollBehavior
                    )
                }
            ) { padding ->
                LazyColumn(Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection)) {
                    item {
                        TitleHeader(
                            titleName = data.app.name,
                            titleBackground = remember { data.app.images?.firstOrNull { it.type == "SuperHeroArt" }?.url },
                            developer = data.app.detail?.developerName.orEmpty(),
                            publisher = data.app.detail?.publisherName.orEmpty(),
                            genres = data.app.detail?.genres.orEmpty(),
                            rating = data.contentWarning
                        )
                    }

                    item {
                        TitleMedia(media = data.app.images.orEmpty())
                    }

                    item {
                        TitleDescriptionCard(
                            short = data.app.detail?.shortDescription.orEmpty(),
                            long = data.app.detail?.description.orEmpty()
                        )
                    }

                    if (data.appPersonal.achievement != null) {
                        item {
                            TitleAchievementCard(data.appPersonal.achievement)
                        }
                    }

                    item {
                        TitleDevicesCard(
                            data.app.devices,
                            data.app.hardware?.maxDownloadSizeInBytes ?: 0L,
                            data.app.detail?.releaseDate.orEmpty()
                        )
                    }

                    item {
                        TitleCapabilitiesCard(data.matchedCaps)
                    }
                }
            }
        }

        is TitleStoreScreenViewModel.StoreData.Error -> FullScreenError(e = data.e)
        is TitleStoreScreenViewModel.StoreData.Loading -> FullScreenLoading()
    }
}

@HiltViewModel
class TitleStoreScreenViewModel @Inject constructor(
    private val thApi: TitleHubService,
    private val msCapDatabase: MsCapDatabase,
    private val xccsController: XccsController
) : ViewModel() {
    var content by mutableStateOf<StoreData>(StoreData.Loading)
        private set

    suspend fun load(titleId: String) {
        content = try {
            val app = thApi.storeBatch(
                fields = "GamePass,ContentBoard,Detail,Hardware,Image,Video,ProductId",
                body = StoreBatchRequest(titleIds = listOf(titleId))
            )

            val achievementInfo = thApi.personalTitleInfo(xuid = app.xuid, titleId = titleId)

            StoreData.Loaded(
                xuid = app.xuid,
                app = app.titles.first(),
                appPersonal = achievementInfo.titles.first(),
                matchedCaps = app.titles.first().detail!!.attributes.mapNotNull {
                    msCapDatabase.mObjPFCaps[it.name]
                        ?.replace("{0}{1}", " (${it.minimum}-${it.maximum})")
                },
                isInstalled = xccsController.installedOnAnyDevice(app.titles.first().productIds!!),
                contentWarning = TitleContentWarning(app.titles.first().contentBoards ?: emptyList())
            )
        } catch (e: Exception) {
            StoreData.Error(e)
        }
    }

    sealed class StoreData {
        class Loaded(
            val xuid: String,
            val app: Title,
            val appPersonal: Title,
            val matchedCaps: List<String>,
            val isInstalled: Boolean,
            val contentWarning: TitleContentWarning
        ) : StoreData()

        class Error(val e: Exception): StoreData()
        object Loading : StoreData()
    }

    inner class TitleContentWarning(
        ratings: List<TitleContentWarnings>
    ) {
        private val matchingRating = ratings
            .filter { msCapDatabase.mObjRatings.containsKey(it.ratingSystem) }
            .minBy { msCapDatabase.mObjRatings[it.ratingSystem]?.markets?.firstOrNull()?.priority ?: Int.MAX_VALUE }
            .let { it to msCapDatabase.mObjRatings[it.ratingSystem]!! }

        private val boardLocalized = matchingRating.second.localizedProperties.first()

        val rating = matchingRating.second.ratings[matchingRating.first.ratingId]!!
        val ratingLocalized = rating.localized.first()

        val interactiveElements = boardLocalized.interactiveElements.associateBy { it.key }.let { map ->
            matchingRating.first.interactiveElements.mapNotNull { map[it] }
        }

        val disclaimers = boardLocalized.descriptors.associateBy { it.key }.let { map ->
            matchingRating.first.disclaimers.mapNotNull { map[it] }
        }

        val descriptors = boardLocalized.descriptors.associateBy { it.key }.let { map ->
            matchingRating.first.descriptors.mapNotNull { map[it] }
        }
    }
}

// Components

@Composable
private fun TitleHeader(
    titleName: String,
    titleBackground: String?,
    developer: String,
    publisher: String,
    genres: List<String>,
    rating: TitleStoreScreenViewModel.TitleContentWarning
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

            Row(verticalAlignment = Alignment.CenterVertically) {
                TitleInstallButton(emptyList())

                Spacer(modifier = Modifier.weight(1f))

                TitleRatingButton(rating)
            }
        }

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
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
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
    size: Long,
    releaseDate: String
) {
    val ctx = LocalContext.current

    val formattedSize = remember(size) { "approx. ${Formatter.formatFileSize(ctx, size)}" }
    // val formattedReleaseDate = remember(releaseDate) { TimeUtils.msDateToLocal(releaseDate) }

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

        TitleSmallCell("Application size", formattedSize)
        // Spacer(modifier = Modifier.height(2.dp))
        // TitleSmallCell("Release date", formattedReleaseDate)
    }
}

@Composable
private fun TitleSmallCell(
    first: String,
    second: String
) {
    Box(Modifier.fillMaxWidth()) {
        Text(
            text = first,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.align(Alignment.CenterStart)
        )
        Text(
            text = second,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.align(Alignment.CenterEnd)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TitleCapabilitiesCard(
    capabilities: List<String>
) {
    Column(Modifier.padding(16.dp)) {
        Text(text = "Capabilities", fontSize = 19.sp, color = MaterialTheme.colorScheme.onSurface)
        FlowRow(Modifier.fillMaxWidth()) {
            capabilities.forEach { cap ->
                AssistChip(onClick = {}, label = { Text(cap) })
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

        Column(
            Modifier
                .padding(start = 16.dp)
                .align(Alignment.CenterVertically)
        ) {
            Text(text = name, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(
                text = joinedDevs,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun TitleDescriptionCard(
    short: String,
    long: String
) {
    val shouldShowExpand = remember(short, long) { short != long }
    val expanded = remember { mutableStateOf(false) }

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
            text = if (expanded.value) long else short,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        if (shouldShowExpand) {
            Text(
                text = if (expanded.value) "Less" else "More",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded.value = !expanded.value }
                    .padding(vertical = 4.dp),
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun TitleRatingButton(rating: TitleStoreScreenViewModel.TitleContentWarning) {
    var showAlert by remember { mutableStateOf(false) }

    if (showAlert) {
        val content = remember(rating) {
            val string = StringBuilder()

            string.append(rating.descriptors.joinToString { it.descriptor })

            if (rating.interactiveElements.isNotEmpty()) {
                string.appendLine().appendLine().append(rating.interactiveElements.joinToString { it.interactiveElement })
            }

            if (rating.disclaimers.isNotEmpty()) {
                string.appendLine().appendLine().append(rating.disclaimers.joinToString { it.descriptor })
            }

            string.toString()
        }

        AlertDialog(
            onDismissRequest = { showAlert = false },
            title = { Text(stringResource(id = R.string.alert_content_warnings)) },
            icon = { Icon(Icons.Default.Warning, contentDescription = null) },
            text = { Text(content) },
            confirmButton = {
                TextButton(onClick = { showAlert = false }) {
                    Text(stringResource(id = R.string.dismiss))
                }
            }
        )
    }

    Button(
        onClick = { showAlert = true }, colors = ButtonDefaults.buttonColors(
            containerColor = Color.White.copy(alpha = 0.2f),
            contentColor = Color.White
        )
    ) {
        if (rating.ratingLocalized.logoUrl != null) {
            AsyncImage(
                model = rating.ratingLocalized.logoUrl,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )

            Spacer(Modifier.width(8.dp))
        }

        Text(rating.ratingLocalized.longName)
    }
}
