package bruhcollective.itaysonlab.jetibox.ui.screens

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Immutable
import bruhcollective.itaysonlab.jetibox.R

@Immutable
enum class Screen(
  val route: String,
  @StringRes val title: Int = 0,
) {
  // internal
  NavGraph("nav_graph"),
  CoreLoading("coreLoading"),
  // landing + auth
  LandingPage("landing/core"),
  // store
  GameDetail("game/{id}"),
  // library
  ViewScreenshot("capture/screenshot/{json}"),
  ViewGameclip("capture/gameclip/{json}"),
  // bottom
  Home("home", title = R.string.tab_home),
  Library("library", title = R.string.tab_library),
  Profile("profile", title = R.string.tab_profile);

  companion object {
    val hideNavigationBar = setOf(CoreLoading.route, LandingPage.route, Dialog.AuthDisclaimer.route)
    val showInBottomNavigation = mapOf(
      Home to Icons.Default.Home,
      Library to Icons.Default.LocalLibrary,
      Profile to Icons.Default.Person,
    )
  }
}

@Immutable
enum class Dialog(
  val route: String
) {
  AuthDisclaimer("dialogs/disclaimers"),
  Logout("dialogs/logout")
}

@Immutable
enum class BottomSheet(
  val route: String
) {

}
