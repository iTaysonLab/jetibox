package bruhcollective.itaysonlab.jetibox.ui.screens.landing

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bruhcollective.itaysonlab.jetibox.ui.LambdaNavigationController
import bruhcollective.itaysonlab.jetibox.R
import bruhcollective.itaysonlab.jetibox.core.xal_bridge.XalBridge
import bruhcollective.itaysonlab.jetibox.core.xbl_bridge.XblUserController
import bruhcollective.itaysonlab.jetibox.ui.screens.Screen
import com.microsoft.xalwrapper.models.XalUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@Composable
fun LandingScreen(
    lambdaNavigationController: LambdaNavigationController,
    viewModel: LandingScreenViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val (snackbarContent, setSnackbarContent) = remember { mutableStateOf("", neverEqualPolicy()) }

    LaunchedEffect(snackbarContent) {
        if (snackbarContent.isNotEmpty()) {
            snackbarHostState.showSnackbar(snackbarContent)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 32.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.auth_welcome),
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Text(
                text = stringResource(R.string.auth_welcome_text),
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp)
                .fillMaxWidth()
        ) {
            Button(onClick = { viewModel.launchLogin(
                onSuccess = { lambdaNavigationController.navigateAndClearStack(Screen.Home) },
                onFailure = { setSnackbarContent(it.message) }
            ) }) {
                if (viewModel.isSignInProcess) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text(text = stringResource(id = R.string.auth_sign))
                }
            }

            TextButton(onClick = { }) {
                Text(text = stringResource(id = R.string.auth_disclaimer))
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomStart),
            snackbar = { data ->
                Snackbar(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(12.dp),
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    snackbarData = data
                )
            }
        )
    }
}

@HiltViewModel
class LandingScreenViewModel @Inject constructor(
    private val xalBridge: XalBridge,
    private val xblUserController: XblUserController,
) : ViewModel() {
    var isSignInProcess by mutableStateOf(false)

    fun launchLogin(
        onSuccess: (XalUser) -> Unit,
        onFailure: (XalBridge.XalBridgeSemaphore.Error<*>) -> Unit
    ) = viewModelScope.launch {
        isSignInProcess = true

        when (val result = xalBridge.requestSignIn()) {
            is XalBridge.XalBridgeSemaphore.Error -> onFailure(result)
            is XalBridge.XalBridgeSemaphore.Success -> {
                xblUserController.reload()
                onSuccess(result.result)
            }
        }

        isSignInProcess = false
    }
}