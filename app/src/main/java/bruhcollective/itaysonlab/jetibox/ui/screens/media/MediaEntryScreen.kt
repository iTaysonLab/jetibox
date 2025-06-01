package bruhcollective.itaysonlab.jetibox.ui.screens.media

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.text.format.Formatter
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.HdrOn
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.ShutterSpeed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled._30fps
import androidx.compose.material.icons.filled._60fps
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import bruhcollective.itaysonlab.jetibox.core.models.mediahub.Screenshot
import bruhcollective.itaysonlab.jetibox.core.models.titlehub.Title
import bruhcollective.itaysonlab.jetibox.core.util.GalleryUtils
import bruhcollective.itaysonlab.jetibox.core.util.TimeUtils
import bruhcollective.itaysonlab.jetibox.core.xbl_bridge.XblTitleDatabase
import bruhcollective.itaysonlab.jetibox.ui.navigation.LocalNavigationWrapper
import bruhcollective.itaysonlab.jetibox.ui.shared.FullScreenLoading
import coil.compose.AsyncImage
import com.squareup.moshi.Moshi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okio.ByteString.Companion.decodeBase64
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaEntryScreen(
    json: String,
    isGameclip: Boolean,
    viewModel: MediaEntryViewModel = hiltViewModel()
) {
    val navWrapper = LocalNavigationWrapper.current

    LaunchedEffect(Unit) { viewModel.loadTitle(navWrapper.context(), json, isGameclip) }

    when (val state = viewModel.state) {
        MediaEntryViewModel.State.Loading -> FullScreenLoading()
        is MediaEntryViewModel.State.Ready -> {
            Scaffold(
                topBar = {
                    TopAppBar(title = {
                        HeaderSmall(
                            image = state.title.displayImage,
                            title = stringResource(id = if (isGameclip) R.string.gameclip else R.string.screenshot),
                            subtitle = state.title.name,
                        )
                    }, navigationIcon = {
                        IconButton(onClick = { navWrapper.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null)
                        }
                    })
                }
            ) { padding ->
                Surface(
                    tonalElevation = 1.dp, modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                ) {
                    LazyColumn {
                        item {
                            MediaEntryImage(image = state.thumbnailUrl)
                        }

                        item {
                            Category(text = stringResource(id = R.string.media_cg_actions))
                        }

                        items(state.actions) { item ->
                            ListItem(
                                leadingContent = {
                                    Icon(item.icon, contentDescription = null)
                                }, headlineContent = {
                                    Text(stringResource(id = item.title))
                                }, modifier = Modifier.clickable {
                                    item.action()
                                }
                            )
                        }

                        item {
                            Category(text = stringResource(id = R.string.media_cg_props))
                        }

                        items(state.properties) { item ->
                            ListItem(
                                leadingContent = {
                                    Icon(item.icon, contentDescription = null)
                                }, headlineContent = {
                                    Text(stringResource(id = item.title))
                                }, supportingContent = {
                                    Text(item.subtitle)
                                }
                            )
                        }
                    }
                }

                viewModel.currentDialog()
            }
        }
    }
}

@HiltViewModel
class MediaEntryViewModel @Inject constructor(
    private val xblTitleDatabase: XblTitleDatabase,
    private val moshi: Moshi
) : ViewModel() {
    var currentDialog by mutableStateOf<@Composable () -> Unit>({})

    var state by mutableStateOf<State>(State.Loading)
        private set

    suspend fun loadTitle(context: Context, json: String, isGameclip: Boolean) {
        if (state != State.Loading) return

        val entry = moshi.adapter(
            if (isGameclip) Gameclip::class.java else Screenshot::class.java
        ).fromJson(json.decodeBase64()!!.string(Charsets.UTF_8))!!

        val title = xblTitleDatabase.getTitles(listOf(entry.getGameId())).values.first()

        val fullResLocator = entry.getLocators().filterIsInstance<ContentLocator.Download>().first()
        val hdrLocator = entry.getLocators().filterIsInstance<ContentLocator.DownloadHDR>().firstOrNull()
        val thumbnail = entry.getLocators().filterIsInstance<ContentLocator.LargeThumbnail>().first()
        val hasHdr = hdrLocator != null

        val actions = buildList {
            add(Action(Icons.Default.Save, R.string.media_action_download) {
                if (hasHdr) {
                    promptForHdr()
                } else {
                    viewModelScope.launch { requestToDownload(context, state as State.Ready) }
                }
            })

            // add(Action(Icons.Default.Delete, R.string.media_action_delete) {})
        }

        val properties = buildList {
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
                    TimeUtils.msDateToLocal(entry.getDate(), withTime = true)
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
        }

        state = State.Ready(
            title = title,
            isGameclip = isGameclip,
            fullResLocator = fullResLocator,
            hdrLocator = hdrLocator,
            thumbnailUrl = if (isGameclip) thumbnail.uri else fullResLocator.uri,
            actions = actions,
            properties = properties,
            fileName = entry.formatFilename()
        )
    }

    private fun promptForHdr() {
        currentDialog = {
            val context = LocalContext.current

            val dlFunc = { hdr: Boolean ->
                currentDialog = {}
                viewModelScope.launch { requestToDownload(context, state as State.Ready, hdr = hdr) }
            }

            AlertDialog(
                onDismissRequest = { currentDialog = {} },
                icon = { Icon(Icons.Default.HdrOn, contentDescription = null) },
                title = { Text(stringResource(id = R.string.media_save_hdr)) },
                text = { Text(stringResource(id = R.string.media_save_hdr_text)) },
                confirmButton = {
                    TextButton(onClick = { dlFunc(true) }) {
                        Text(stringResource(id = R.string.yes))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { dlFunc(false) }) {
                        Text(stringResource(id = R.string.no))
                    }
                },
            )
        }
    }

    private suspend fun requestToDownload(context: Context, state: State.Ready, hdr: Boolean = false) {
        GalleryUtils.saveToGallery(
            ctx = context,
            url = if (hdr) state.hdrLocator?.uri ?: state.fullResLocator.uri else state.fullResLocator.uri,
            video = state.isGameclip,
            filename = state.fileName + if (hdr) "_HDR" else "",
            hdr = hdr
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
                            delay(500L)
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

    sealed class State {
        object Loading : State()

        class Ready(
            val title: Title,
            val fileName: String,
            val isGameclip: Boolean,
            val fullResLocator: ContentLocator.Download,
            val hdrLocator: ContentLocator.DownloadHDR?,
            val thumbnailUrl: String,
            val actions: List<Action>,
            val properties: List<Property>
        ) : State()
    }
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