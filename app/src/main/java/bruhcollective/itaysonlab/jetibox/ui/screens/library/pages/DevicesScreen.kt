package bruhcollective.itaysonlab.jetibox.ui.screens.library.pages

import android.text.format.Formatter
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bruhcollective.itaysonlab.jetibox.R
import bruhcollective.itaysonlab.jetibox.core.models.xccs.Device
import bruhcollective.itaysonlab.jetibox.core.models.xccs.DevicePowerState
import bruhcollective.itaysonlab.jetibox.core.xbl_bridge.XccsController
import bruhcollective.itaysonlab.jetibox.ui.navigation.LocalNavigationWrapper
import bruhcollective.itaysonlab.jetibox.ui.shared.FullScreenError
import bruhcollective.itaysonlab.jetibox.ui.shared.FullScreenLoading
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okio.ByteString.Companion.encodeUtf8
import javax.inject.Inject

@Composable
fun DevicesScreen(
    viewModel: DevicesViewModel = hiltViewModel()
) {
    val navWrapper = LocalNavigationWrapper.current

    when (val state = viewModel.state) {
        DevicesViewModel.State.Loading -> FullScreenLoading()
        is DevicesViewModel.State.Error -> FullScreenError(state.e)
        is DevicesViewModel.State.Ready -> {
            SwipeRefresh(state = rememberSwipeRefreshState(viewModel.isReloading), onRefresh = { viewModel.reload() }) {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.devices) { item ->
                        DeviceCard(item) {
                            navWrapper.navigate("console/${viewModel.generateUrl(item)}")
                        }
                    }
                }
            }
        }
    }
}

@HiltViewModel
class DevicesViewModel @Inject constructor(
    private val json: Json,
    private val xccsController: XccsController
) : ViewModel() {
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
            load()
            isReloading = false
        }
    }

    private suspend fun load() {
        state = try {
            State.Ready(xccsController.consoles())
        } catch (e: Exception) {
            State.Error(e)
        }
    }

    fun generateUrl(device: Device) = json.encodeToString(device).encodeUtf8().base64Url()

    sealed class State {
        class Ready(val devices: List<Device>) : State()
        class Error(val e: Exception) : State()
        object Loading : State()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeviceCard(
    device: Device,
    onClick: () -> Unit
) {
    val ctx = LocalContext.current

    val subtitle = remember(device.consoleType, device.powerState) {
        device.consoleType.productName + " • " + ctx.getString(
            when (device.powerState) {
                DevicePowerState.ConnectedStandby -> R.string.device_state_standby
                DevicePowerState.On -> R.string.device_state_on
                DevicePowerState.Off -> R.string.device_state_off
            }
        )
    }

    val storageProgress = remember(device.storageDevices) {
        val first = device.storageDevices.first()
        val taken = first.totalSpaceBytes - first.freeSpaceBytes
        val progress = (taken.toFloat() / first.totalSpaceBytes).coerceIn(0f..1f)

        Triple(
            progress, ctx.getString(R.string.device_storage_free, Formatter.formatFileSize(ctx, first.freeSpaceBytes)), Formatter.formatFileSize(ctx, first.totalSpaceBytes)
        )
    }

    Card(
        Modifier
            .fillMaxSize()
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)) {
        Column(Modifier.padding(16.dp)) {
            Text(text = device.name, fontSize = 21.sp, color = MaterialTheme.colorScheme.onSurface)
            Text(
                text = subtitle,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        Column(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceColorAtElevation(16.dp)).padding(16.dp)) {
            LinearProgressIndicator(progress = storageProgress.first, modifier = Modifier.fillMaxSize())

            Spacer(modifier = Modifier.height(4.dp))

            Box(Modifier.fillMaxWidth()) {
                Text(
                    text = storageProgress.second,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.align(Alignment.CenterStart)
                )

                Text(
                    text = storageProgress.third,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
        }
    }
}