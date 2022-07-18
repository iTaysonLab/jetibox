package bruhcollective.itaysonlab.jetibox.ui

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import bruhcollective.itaysonlab.jetibox.core.xal_bridge.XalBridge
import bruhcollective.itaysonlab.jetibox.core.xbl_bridge.XblUserController
import bruhcollective.itaysonlab.jetibox.ui.screens.BottomSheet
import bruhcollective.itaysonlab.jetibox.ui.screens.Dialog
import bruhcollective.itaysonlab.jetibox.ui.screens.Screen
import bruhcollective.itaysonlab.jetibox.ui.screens.home.HomeScreen
import bruhcollective.itaysonlab.jetibox.ui.screens.landing.LandingScreen
import bruhcollective.itaysonlab.jetibox.ui.screens.store.TitleStoreScreen

@Composable
fun AppNavigation(
  navController: NavHostController,
  provideLambdaController: LambdaNavigationController,
  xalBridge: XalBridge,
  xblUserController: XblUserController,
  modifier: Modifier
) {
  LaunchedEffect(Unit) {
    val signedIn = if (!xalBridge.initialized) {
      xblUserController.tryRestoring()
      xalBridge.initialize()
      (xalBridge.tryUsingSavedData() is XalBridge.XalBridgeSemaphore.Success).also { xblUserController.reload() }
    } else {
      xalBridge.currentProfile != null
    }

    navController.navigate(
      (if (signedIn) Screen.Home else Screen.LandingPage).route
    ) {
      popUpTo(Screen.NavGraph.route)
    }
  }

  NavHost(
    navController = navController,
    startDestination = Screen.CoreLoading.route,
    route = Screen.NavGraph.route,
    modifier = modifier
  ) {
    composable(Screen.CoreLoading.route) {
      Box(Modifier.fillMaxSize()) {
        CircularProgressIndicator(modifier = Modifier
          .align(Alignment.Center)
          .size(56.dp))
      }
    }

    composable(Screen.LandingPage.route) {
      LandingScreen(provideLambdaController)
    }

    composable(Screen.Home.route) {
      HomeScreen(provideLambdaController)
    }

    composable(Screen.GameDetail.route) {
      TitleStoreScreen(provideLambdaController, it.arguments!!.getString("id")!!)
    }

    dialog(Dialog.Logout.route) {
      /*AlertDialog(onDismissRequest = { navController.popBackStack() }, icon = {
        Icon(Icons.Rounded.Warning, null)
      }, title = {
        Text(stringResource(id = R.string.logout_title))
      }, text = {
        Text(stringResource(id = R.string.logout_message))
      }, confirmButton = {
        TextButton(onClick = {
          navController.popBackStack()
          //authManager.reset()
          //android.os.Process.killProcess(android.os.Process.myPid()) // TODO: maybe dynamic restart the session instances?
        }) {
          Text(stringResource(id = R.string.logout_confirm))
        }
      }, dismissButton = {
        TextButton(onClick = { navController.popBackStack() }) {
          Text(stringResource(id = R.string.logout_cancel))
        }
      })*/
    }
  }
}

@JvmInline
@Immutable
value class LambdaNavigationController(
  val controller: () -> NavHostController
) {
  @Suppress("DeprecatedCallableAddReplaceWith")
  @Deprecated(message = "Migrate to navigate(Screen) or navigate(Dialog) if not using arguments")
  fun navigate(route: String) = controller().navigate(route)

  fun navigate(screen: Screen) = controller().navigate(screen.route)
  fun navigate(dialog: Dialog) = controller().navigate(dialog.route)

  fun navigate(sheet: BottomSheet, args: Map<String, String>) {
    var url = sheet.route

    args.forEach { entry ->
      url = url.replace("{${entry.key}}", entry.value)
    }

    controller().navigate(url)
  }

  fun navigateAndClearStack(screen: Screen) = controller().navigate(screen.route) { popUpTo(Screen.NavGraph.route) }

  fun popBackStack() = controller().popBackStack()

  fun context() = controller().context
  fun string(@StringRes id: Int) = context().getString(id)
  fun openInBrowser(uri: String) = context().startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse(uri)))
}
