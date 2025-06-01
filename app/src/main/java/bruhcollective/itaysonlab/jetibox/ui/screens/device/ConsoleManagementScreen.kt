package bruhcollective.itaysonlab.jetibox.ui.screens.device

import android.text.format.Formatter
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bruhcollective.itaysonlab.jetibox.R
import bruhcollective.itaysonlab.jetibox.core.models.titlehub.Title
import bruhcollective.itaysonlab.jetibox.core.models.xccs.Device
import bruhcollective.itaysonlab.jetibox.core.models.xccs.InstalledApp
import bruhcollective.itaysonlab.jetibox.core.models.xccs.StorageDevice
import bruhcollective.itaysonlab.jetibox.core.util.TimeUtils
import bruhcollective.itaysonlab.jetibox.core.xbl_bridge.XblTitleDatabase
import bruhcollective.itaysonlab.jetibox.core.xbl_bridge.XccsController
import bruhcollective.itaysonlab.jetibox.ui.navigation.LocalNavigationWrapper
import bruhcollective.itaysonlab.jetibox.ui.shared.FullScreenError
import bruhcollective.itaysonlab.jetibox.ui.shared.FullScreenLoading
import bruhcollective.itaysonlab.jetibox.ui.shared.components.FullscreenDoneDialog
import bruhcollective.itaysonlab.jetibox.ui.shared.components.FullscreenErrorDialog
import bruhcollective.itaysonlab.jetibox.ui.shared.components.FullscreenLoadingDialog
import coil.compose.AsyncImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okio.ByteString.Companion.decodeBase64
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ConsoleManagementScreen(
    json: String,
    viewModel: ConsoleManagementViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) { viewModel.load(json) }

    val navWrapper = LocalNavigationWrapper.current

    when (val state = viewModel.state) {
        is ConsoleManagementViewModel.State.Error -> FullScreenError(state.e)
        ConsoleManagementViewModel.State.Loading -> FullScreenLoading()

        is ConsoleManagementViewModel.State.Ready -> {
            Scaffold(
                topBar = {
                    TopAppBar(title = {
                        Column {
                            Text(
                                text = state.device.name,
                                fontSize = 16.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = state.device.consoleType.productName,
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }, navigationIcon = {
                        IconButton(onClick = { navWrapper.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null)
                        }
                    }, actions = {
                        IconButton(onClick = { viewModel.renameConsole(state) }) {
                            Icon(Icons.Default.Edit, contentDescription = null)
                        }
                    })
                }
            ) { padding ->
                Column(Modifier.padding(padding)) {
                    ConsoleStorageHeader(device = state.primaryStorage)

                    Surface(
                        tonalElevation = 1.dp, modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    ) {
                        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(viewModel.stateList/*, key = { it.titleId }*/) { item ->
                                val title = remember(item.titleId) { state.titles[item.titleId]!! }

                                val linkedDlc = remember(item.legacyProductId) {
                                    state.dlc.getOrElse(
                                        item.legacyProductId.orEmpty(),
                                    ) {
                                        emptyList()
                                    }
                                }

                                InstalledAppCard(
                                    icon = title.displayImage,
                                    name = title.name,
                                    sizeInBytes = item.sizeInBytes,
                                    linkedDlc = linkedDlc,
                                    onCardClick = { navWrapper.navigate("game/${item.titleId}") },
                                    onDeleteClick = { viewModel.deleteApplication(state, item) },
                                    onDlcClick = { viewModel.showDlcs(linkedDlc) },
                                    modifier = Modifier.animateItem()
                                )
                            }
                        }
                    }
                }

                viewModel.currentDialog()
            }
        }
    }
}

@HiltViewModel
class ConsoleManagementViewModel @Inject constructor(
    private val json: Json,
    private val xccsController: XccsController,
    private val xblTitleDatabase: XblTitleDatabase,
) : ViewModel() {
    var state by mutableStateOf<State>(State.Loading)
        private set

    var stateList by mutableStateOf<List<InstalledApp>>(emptyList())
        private set

    var currentDialog by mutableStateOf<@Composable () -> Unit>({})
        private set

    suspend fun load(json: String) {
        state = try {
            val device = this.json.decodeFromString<Device>(json.decodeBase64()!!.string(Charsets.UTF_8))

            val apps = xccsController.installedApps(device.id)
            val titles = xblTitleDatabase.getTitles(apps.map { it.titleId }.filter { it > 0 })

            val dlcMap = mutableMapOf<String, MutableList<InstalledApp>>()

            apps.filter { it.contentType == "Dlc" && it.parentId != null }.map { app ->
                if (dlcMap.containsKey(app.parentId)) {
                    dlcMap[app.parentId]?.add(app)
                } else {
                    dlcMap.put(app.parentId!!, mutableListOf(app))
                }
            }

            stateList = apps.filter { it.contentType == "Game" }
                .sortedByDescending { TimeUtils.msDateToUnix(it.updateTime ?: it.installTime) }
            State.Ready(device = device, apps = apps, titles = titles, dlc = dlcMap, primaryStorage = device.storageDevices.first { it.isDefault })
        } catch (e: Exception) {
            e.printStackTrace()
            State.Error(e)
        }
    }

    fun showDlcs(linkedDlc: List<InstalledApp>) {
        currentDialog = {
            val scrollState = rememberScrollState()

            AlertDialog(
                onDismissRequest = { currentDialog = {} },
                icon = { Icon(Icons.Default.AutoAwesome, contentDescription = null) },
                title = { Text(stringResource(id = R.string.app_dlc_alert)) },
                text = {
                    Column(
                        Modifier
                            .heightIn(min = 0.dp, max = 200.dp)
                            .verticalScroll(state = scrollState)) {
                        linkedDlc.forEach { dlc ->
                            Text(text = dlc.name, modifier = Modifier)
                        }
                    }
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
    }

    fun renameConsole(state: State.Ready) {
        currentDialog = {
            var consoleName by remember { mutableStateOf(TextFieldValue(state.device.name)) }

            AlertDialog(
                onDismissRequest = { currentDialog = {} },
                icon = { Icon(Icons.Default.Edit, contentDescription = null) },
                title = { Text(stringResource(id = R.string.console_rename)) },
                text = {
                    TextField(placeholder = {
                        Text(stringResource(id = R.string.console_rename_hint))
                    }, value = consoleName, onValueChange = { consoleName = it })
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (state.device.name == consoleName.text || consoleName.text.isEmpty() || consoleName.text.isBlank()) {
                            currentDialog = {}
                            return@TextButton
                        }

                        viewModelScope.launch { renameConsoleImpl(state, consoleName.text) }
                    }) {
                        Text(
                            stringResource(id = R.string.confirm)
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { currentDialog = {} }) {
                        Text(
                            stringResource(id = R.string.dismiss)
                        )
                    }
                },
            )
        }
    }

    private suspend fun renameConsoleImpl(state: State.Ready, to: String) {
        currentDialog = { FullscreenLoadingDialog() }

        xccsController.renameConsole(state.device.id, to)
        this.state = state.copy(device = state.device.copy(name = to))

        currentDialog = { FullscreenDoneDialog(onDismissRequest = { currentDialog = {} }) }
    }

    fun deleteApplication(state: State.Ready, app: InstalledApp) {
        currentDialog = {
            AlertDialog(
                onDismissRequest = { currentDialog = {} },
                icon = { Icon(Icons.Default.Delete, contentDescription = null) },
                title = { Text(stringResource(id = R.string.app_delete_title)) },
                text = { Text(stringResource(id = R.string.app_delete_text, app.name, state.device.name)) },
                confirmButton = {
                    TextButton(onClick = {
                        viewModelScope.launch { deleteApplicationImpl(state, app) }
                    }) {
                        Text(
                            stringResource(id = R.string.confirm)
                        )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { currentDialog = {} }) {
                        Text(
                            stringResource(id = R.string.dismiss)
                        )
                    }
                },
            )
        }
    }

    private suspend fun deleteApplicationImpl(state: State.Ready, app: InstalledApp) {
        currentDialog = { FullscreenLoadingDialog() }

        when (xccsController.deleteApplication(state.device.id, app.instanceId)) {
            XccsController.ExecutionResult.Success -> {
                this.state = state.copy(
                    primaryStorage = state.primaryStorage.copy(freeSpaceBytes = state.primaryStorage.freeSpaceBytes + app.sizeInBytes),
                    apps = state.apps.filterNot { it.instanceId == app.instanceId }
                )

                this.stateList = stateList.filterNot { it.instanceId == app.instanceId }

                currentDialog = { FullscreenDoneDialog(onDismissRequest = { currentDialog = {} }) }
            }

            else -> {
                currentDialog = { FullscreenErrorDialog(onDismissRequest = { currentDialog = {} }) }
            }
        }
    }

    sealed class State {
        class Error(val e: Exception) : State()
        object Loading : State()

        data class Ready(
            val device: Device,
            val apps: List<InstalledApp>,
            val titles: Map<Long, Title>,
            val dlc: Map<String, List<InstalledApp>>,
            val primaryStorage: StorageDevice
        ) : State()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConsoleStorageHeader(
    device: StorageDevice
) {
    val ctx = LocalContext.current

    val storageProgress = remember(device) {
        val taken = device.totalSpaceBytes - device.freeSpaceBytes
        val progress = (taken.toFloat() / device.totalSpaceBytes).coerceIn(0f..1f)
        Triple(progress, device.storageDeviceName, ctx.getString(R.string.device_storage_free_of, Formatter.formatFileSize(ctx, device.freeSpaceBytes), Formatter.formatFileSize(ctx, device.totalSpaceBytes)))
    }

    val indicatorProgress = animateFloatAsState(targetValue = storageProgress.first)

    Card(
        Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 12.dp)) {
        Column {
            Column(Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                Text(
                    text = storageProgress.second,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                )

                Text(
                    text = storageProgress.third,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                )
            }

            LinearProgressIndicator(progress = indicatorProgress.value, modifier = Modifier.fillMaxWidth(), trackColor = MaterialTheme.colorScheme.surfaceColorAtElevation(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InstalledAppCard(
    icon: String,
    name: String,
    sizeInBytes: Long,
    linkedDlc: List<InstalledApp>,
    onCardClick: () -> Unit,
    onDlcClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier
) {
    val ctx = LocalContext.current

    val formattedSize = remember(sizeInBytes) { Formatter.formatFileSize(ctx, sizeInBytes) }
    val haveDlc = remember(linkedDlc) { linkedDlc.isNotEmpty() }

    Card(
        modifier
            .fillMaxSize()
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onCardClick)) {
        Column {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(model = icon, contentDescription = null, modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp)), placeholder = ColorPainter(MaterialTheme.colorScheme.surface))

                Column(
                    Modifier
                        .padding(horizontal = 12.dp)
                        .weight(1f)) {
                    Text(text = name, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(text = formattedSize, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                }

                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                }
            }

            if (haveDlc) {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceColorAtElevation(16.dp))
                    .clickable(onClick = onDlcClick)
                    .padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {

                    Text(
                        text = stringResource(id = R.string.app_dlc_installcount, linkedDlc.size),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.weight(1f)
                    )

                    Icon(Icons.Default.ChevronRight, contentDescription = null)
                }
            }
        }
    }
}