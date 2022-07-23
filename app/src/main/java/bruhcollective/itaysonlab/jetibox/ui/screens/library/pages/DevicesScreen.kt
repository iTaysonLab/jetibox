package bruhcollective.itaysonlab.jetibox.ui.screens.library.pages

import android.text.format.Formatter
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import bruhcollective.itaysonlab.jetibox.core.service.XccsService
import bruhcollective.itaysonlab.jetibox.ui.navigation.LocalNavigationWrapper
import bruhcollective.itaysonlab.jetibox.ui.shared.FullScreenError
import bruhcollective.itaysonlab.jetibox.ui.shared.FullScreenLoading
import com.squareup.moshi.Moshi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
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

@HiltViewModel
class DevicesViewModel @Inject constructor(
    private val moshi: Moshi,
    private val xccsService: XccsService
) : ViewModel() {
    var state by mutableStateOf<State>(State.Loading)
        private set

    init {
        viewModelScope.launch {
            state = try {
                State.Ready(xccsService.listDevices().result)
            } catch (e: Exception) {
                State.Error(e)
            }
        }
    }

    fun generateUrl(device: Device) = moshi.adapter(Device::class.java).toJson(device).encodeUtf8().base64Url()

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