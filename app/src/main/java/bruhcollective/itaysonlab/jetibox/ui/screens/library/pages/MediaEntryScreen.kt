package bruhcollective.itaysonlab.jetibox.ui.screens.library.pages

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.text.format.Formatter
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bruhcollective.itaysonlab.jetibox.R
import bruhcollective.itaysonlab.jetibox.core.models.mediahub.ContentLocator
import bruhcollective.itaysonlab.jetibox.core.models.mediahub.Gameclip
import bruhcollective.itaysonlab.jetibox.core.models.mediahub.MediaHubEntry
import bruhcollective.itaysonlab.jetibox.core.models.titlehub.Title
import bruhcollective.itaysonlab.jetibox.core.util.GalleryUtils
import bruhcollective.itaysonlab.jetibox.core.util.TimeUtils
import bruhcollective.itaysonlab.jetibox.core.xbl_bridge.XblTitleDatabase
import bruhcollective.itaysonlab.jetibox.ui.navigation.LocalNavigationWrapper
import bruhcollective.itaysonlab.jetibox.ui.shared.FullScreenLoading
import coil.compose.AsyncImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaEntryScreen(
    entry: MediaHubEntry,
    viewModel: MediaEntryViewModel = hiltViewModel()
) {
    val navWrapper = LocalNavigationWrapper.current
    val isGameclip = remember(entry) { entry is Gameclip }
    val fullResLocator =
        remember(entry) { entry.getLocators().filterIsInstance<ContentLocator.Download>().first() }
    val thumbnailLocator = remember(entry) {
        entry.getLocators().filterIsInstance<ContentLocator.LargeThumbnail>().first()
    }

    LaunchedEffect(Unit) { viewModel.loadTitle(navWrapper.context(), entry) }

    if (viewModel.titleState != null) {
        Scaffold(
            topBar = {
                SmallTopAppBar(title = {
                    HeaderSmall(
                        image = viewModel.titleState!!.displayImage,
                        title = stringResource(id = if (isGameclip) R.string.gameclip else R.string.screenshot),
                        subtitle = viewModel.titleState!!.name,
                    )
                }, navigationIcon = {
                    IconButton(onClick = { navWrapper.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                })
            }, modifier = Modifier.statusBarsPadding()
        ) { padding ->
            Surface(
                tonalElevation = 1.dp, modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {

                LazyColumn {
                    item {
                        MediaEntryImage(image = if (isGameclip) thumbnailLocator.uri else fullResLocator.uri)
                    }

                    item {
                        Category(text = stringResource(id = R.string.media_cg_actions))
                    }

                    items(viewModel.entryActions) { item ->
                        ListItem(
                            leadingContent = {
                                Icon(item.icon, contentDescription = null)
                            }, headlineText = {
                                Text(stringResource(id = item.title))
                            }, modifier = Modifier.clickable {
                                item.action()
                            }
                        )
                    }

                    item {
                        Category(text = stringResource(id = R.string.media_cg_props))
                    }

                    items(viewModel.entryProperties) { item ->
                        ListItem(
                            leadingContent = {
                                Icon(item.icon, contentDescription = null)
                            }, headlineText = {
                                Text(stringResource(id = item.title))
                            }, supportingText = {
                                Text(item.subtitle)
                            }
                        )
                    }
                }
            }

            viewModel.currentDialog()
        }
    } else {
        FullScreenLoading()
    }
}

@HiltViewModel
class MediaEntryViewModel @Inject constructor(
    private val xblTitleDatabase: XblTitleDatabase
) : ViewModel() {
    var currentDialog by mutableStateOf<@Composable () -> Unit>({})

    var entryActions = mutableStateListOf<Action>()
    var entryProperties = mutableStateListOf<Property>()

    var titleState by mutableStateOf<Title?>(null)
        private set

    suspend fun loadTitle(context: Context, entry: MediaHubEntry) {
        if (titleState != null) return

        val isGameclip = entry is Gameclip
        val fullResLocator = entry.getLocators().filterIsInstance<ContentLocator.Download>().first()
        val hasHdr = entry.getLocators().filterIsInstance<ContentLocator.DownloadHDR>().isNotEmpty()

        entryActions.addAll(buildList {
            add(Action(Icons.Default.Save, R.string.media_action_download) {
                viewModelScope.launch { requestToDownload(context, entry) }
            })

            // add(Action(Icons.Default.Delete, R.string.media_action_delete) {})
        })

        entryProperties.addAll(buildList {
            add(
                Property(
                    Icons.Default.FileDownload,
                    R.string.media_prop_size,
                    Formatter.formatFileSize(context, fullResLocator.fileSize)
                )
            )

            add(
                Property(
                    Icons.Default.AspectRatio,
                    R.string.media_prop_resolution,
                    entry.formatResolution()
                )
            )

            add(
                Property(
                    Icons.Default.DateRange,
                    R.string.media_prop_date,
                    TimeUtils.msDateToLocal(entry.getDate(), msTimeWithMs = true, withTime = true)
                )
            )

            add(
                Property(
                    Icons.Default.HdrOn,
                    R.string.media_prop_hdr,
                    context.getString(if (hasHdr) R.string.yes else R.string.no)
                )
            )

            if (isGameclip) {
                val gc = entry as Gameclip

                add(
                    Property(
                        Icons.Default.Timer,
                        R.string.media_prop_duration,
                        gc.durationInSeconds.toString()
                    )
                )

                add(
                    Property(
                        when (gc.frameRate) {
                            30 -> Icons.Default._30fps
                            60 -> Icons.Default._60fps
                            else -> Icons.Default.ShutterSpeed
                        }, R.string.media_prop_fps, gc.frameRate.toString()
                    )
                )
            }
        })

        titleState = xblTitleDatabase.getTitles(listOf(entry.getGameId())).values.first()
    }

    private suspend fun requestToDownload(context: Context, entry: MediaHubEntry) {
        GalleryUtils.saveToGallery(
            ctx = context,
            url = entry.getLocators().filterIsInstance<ContentLocator.Download>().first().uri,
            video = entry is Gameclip,
            filename = entry.getEntryId()
        ).collect { state ->
            currentDialog = {
                when (state) {
                    is GalleryUtils.SaveToGalleryState.Downloading -> {
                        AlertDialog(
                            onDismissRequest = { currentDialog = {} },
                            icon = { Icon(Icons.Default.Save, contentDescription = null) },
                            title = { Text(stringResource(id = R.string.saving_dialog)) },
                            text = {
                                Column {
                                    LinearProgressIndicator(
                                        progress = state.progress,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Box(Modifier.fillMaxWidth()) {
                                        Text(
                                            text = state.formattedDownloadedSize,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                            modifier = Modifier.align(Alignment.CenterStart)
                                        )

                                        Text(
                                            text = state.formattedTotalSize,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                            modifier = Modifier.align(Alignment.CenterEnd)
                                        )
                                    }
                                }
                            },
                            confirmButton = {},
                            properties = DialogProperties(
                                dismissOnBackPress = false,
                                dismissOnClickOutside = false
                            )
                        )
                    }

                    is GalleryUtils.SaveToGalleryState.Error -> {
                        val scrollState = rememberScrollState()

                        AlertDialog(
                            onDismissRequest = { currentDialog = {} },
                            icon = { Icon(Icons.Default.Error, contentDescription = null) },
                            title = { Text(stringResource(id = R.string.error)) },
                            text = {
                                Text(
                                    text = state.exception.stackTraceToString(), modifier = Modifier
                                        .heightIn(min = 0.dp, max = 200.dp)
                                        .verticalScroll(state = scrollState)
                                )
                            },
                            confirmButton = {
                                TextButton(onClick = { currentDialog = {} }) {
                                    Text(
                                        stringResource(id = R.string.dismiss)
                                    )
                                }
                            },
                        )
                    }

                    GalleryUtils.SaveToGalleryState.PermissionsRequired -> {
                        AlertDialog(
                            onDismissRequest = { currentDialog = {} },
                            icon = { Icon(Icons.Default.Warning, contentDescription = null) },
                            title = { Text(stringResource(id = R.string.warning_permission)) },
                            text = { Text(stringResource(id = R.string.warning_permission_storage_media)) },
                            confirmButton = {
                                TextButton(onClick = {
                                    context.startActivity(
                                        Intent(
                                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                            Uri.fromParts("package", context.packageName, null)
                                        )
                                    )
                                }) { Text(stringResource(id = R.string.warning_permission_manage)) }
                            },
                            dismissButton = {
                                TextButton(onClick = { currentDialog = {} }) {
                                    Text(
                                        stringResource(id = R.string.dismiss)
                                    )
                                }
                            }
                        )
                    }

                    GalleryUtils.SaveToGalleryState.Success -> {
                        LaunchedEffect(Unit) {
                            delay(250L)
                            currentDialog = {}
                        }

                        AlertDialog(
                            onDismissRequest = { currentDialog = {} },
                            icon = { Icon(Icons.Default.Done, contentDescription = null) },
                            title = { Text(stringResource(id = R.string.done)) },
                            confirmButton = {},
                            properties = DialogProperties(
                                dismissOnBackPress = false,
                                dismissOnClickOutside = false
                            )
                        )
                    }
                }
            }
        }
    }

    class Action(val icon: ImageVector, val title: Int, val action: () -> Unit)
    class Property(val icon: ImageVector, val title: Int, val subtitle: String)
}

@Composable
private fun MediaEntryImage(
    image: String
) {
    AsyncImage(
        model = image,
        placeholder = ColorPainter(
            MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
        ),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp),
        contentScale = ContentScale.FillBounds
    )
}

@Composable
private fun Category(
    text: String
) {
    Text(
        text = text,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        fontSize = 13.sp,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun HeaderSmall(
    image: String,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
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
            Text(text = title, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(text = subtitle, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}