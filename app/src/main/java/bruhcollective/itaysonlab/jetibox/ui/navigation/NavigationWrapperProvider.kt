package bruhcollective.itaysonlab.jetibox.ui.navigation

import android.content.Intent
import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController
import bruhcollective.itaysonlab.jetibox.ui.screens.BottomSheet
import bruhcollective.itaysonlab.jetibox.ui.screens.Dialog
import bruhcollective.itaysonlab.jetibox.ui.screens.Screen

@JvmInline
@Immutable
value class NavigationWrapper(
    val controller: () -> NavHostController
) {
    fun navigateAndClearStack(screen: Screen) = controller().navigate(screen.route) { popUpTo(Screen.NavGraph.route) }
    fun navigate(route: String) = controller().navigate(route)

    fun navigate(screen: Screen) = navigate(screen.route)
    fun navigate(dialog: Dialog) = navigate(dialog.route)

    fun navigate(sheet: BottomSheet, args: Map<String, String>) {
        var url = sheet.route

        args.forEach { entry ->
            url = url.replace("{${entry.key}}", entry.value)
        }

        controller().navigate(url)
    }

    fun popBackStack() = controller().popBackStack()
    fun context() = controller().context

    fun string(@StringRes id: Int) = context().getString(id)
    fun openInBrowser(uri: String) = context().startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse(uri)))
}

val LocalNavigationWrapper = staticCompositionLocalOf<NavigationWrapper> {
    error("You should clearly provide NavigationWrapper for use.")
}