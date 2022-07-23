package bruhcollective.itaysonlab.jetibox.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import bruhcollective.itaysonlab.jetibox.core.models.mediahub.Gameclip
import bruhcollective.itaysonlab.jetibox.core.models.mediahub.Screenshot
import bruhcollective.itaysonlab.jetibox.core.xal_bridge.XalInitController
import bruhcollective.itaysonlab.jetibox.ui.screens.Dialog
import bruhcollective.itaysonlab.jetibox.ui.screens.Screen
import bruhcollective.itaysonlab.jetibox.ui.screens.home.HomeScreen
import bruhcollective.itaysonlab.jetibox.ui.screens.landing.LandingScreen
import bruhcollective.itaysonlab.jetibox.ui.screens.library.LibraryScreen
import bruhcollective.itaysonlab.jetibox.ui.screens.library.pages.MediaEntryScreen
import bruhcollective.itaysonlab.jetibox.ui.screens.profile.ProfileScreen
import bruhcollective.itaysonlab.jetibox.ui.screens.store.TitleStoreScreen
import com.squareup.moshi.Moshi
import okio.ByteString.Companion.decodeBase64

@Composable
fun AppNavigation(
  navController: NavHostController,
  moshi: Moshi,
  xalInitController: XalInitController,
  modifier: Modifier
) {
  LaunchedEffect(Unit) {
    val signedIn = xalInitController.init()

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
      LandingScreen()
    }

    composable(Screen.Home.route) {
      HomeScreen()
    }

    composable(Screen.GameDetail.route) {
      TitleStoreScreen(it.arguments!!.getString("id")!!)
    }

    composable(Screen.Library.route) {
      LibraryScreen()
    }

    composable(Screen.Profile.route) {
      ProfileScreen()
    }

    composable(Screen.ViewScreenshot.route) {
      val json = it.arguments?.getString("json") ?: ""
      val parsedObject = remember(json) { moshi.adapter(Screenshot::class.java).fromJson(json.decodeBase64()!!.string(Charsets.UTF_8))!! }
      MediaEntryScreen(entry = parsedObject)
    }

    composable(Screen.ViewGameclip.route) {
      val json = it.arguments?.getString("json") ?: ""
      val parsedObject = remember(json) { moshi.adapter(Gameclip::class.java).fromJson(json.decodeBase64()!!.string(Charsets.UTF_8))!! }
      MediaEntryScreen(entry = parsedObject)
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
