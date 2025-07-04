package bruhcollective.itaysonlab.jetibox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import bruhcollective.itaysonlab.jetibox.core.xal_bridge.XalInitController
import bruhcollective.itaysonlab.jetibox.core.xbl_bridge.XblUserController
import bruhcollective.itaysonlab.jetibox.ui.AppNavigation
import bruhcollective.itaysonlab.jetibox.ui.navigation.LocalNavigationWrapper
import bruhcollective.itaysonlab.jetibox.ui.navigation.NavigationWrapper
import bruhcollective.itaysonlab.jetibox.ui.screens.Screen
import bruhcollective.itaysonlab.jetibox.ui.theme.ApplicationThemeSource
import bruhcollective.itaysonlab.jetibox.ui.theme.JetiboxTheme
import coil.compose.AsyncImage
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var xalInitController: XalInitController

    @Inject
    lateinit var xblUserController: XblUserController

    @OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            val bottomSheetNavigator = rememberBottomSheetNavigator()
            val navController = rememberNavController(bottomSheetNavigator)
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val lambdaNavController = NavigationWrapper { navController }

            JetiboxTheme(
                xblUserController = xblUserController,
                themeSource = ApplicationThemeSource.DYNAMIC
            ) {
                CompositionLocalProvider(LocalNavigationWrapper provides lambdaNavController) {
                    Scaffold(
                        bottomBar = {
                            val currentDestination = navBackStackEntry?.destination
                            if (Screen.hideNavigationBar.any { it == currentDestination?.route }) return@Scaffold

                            NavigationBar(
                                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)),
                            ) {
                                Screen.showInBottomNavigation.forEach { (screen, icon) ->
                                    NavigationBarItem(
                                        icon = {
                                            if (screen == Screen.Profile) {
                                                AsyncImage(
                                                    model = xblUserController.xblCurrentUserAvatar,
                                                    contentDescription = null,
                                                    modifier = Modifier
                                                        .size(24.dp)
                                                        .clip(CircleShape)
                                                )
                                            } else {
                                                Icon(
                                                    icon,
                                                    contentDescription = stringResource(screen.title)
                                                )
                                            }
                                        },
                                        label = {
                                            Text(
                                                if (screen == Screen.Profile) {
                                                    xblUserController.xblCurrentUserDisplayName
                                                } else {
                                                    stringResource(screen.title)
                                                }
                                            )
                                        },
                                        selected = currentDestination?.hierarchy?.any {
                                            it.hasRoute(route = screen.route, null)
                                        } == true,
                                        onClick = {
                                            navController.navigate(screen.route) {
                                                popUpTo(Screen.NavGraph.route) {
                                                    saveState = true
                                                }

                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    )
                                }
                            }
                        }, contentWindowInsets = WindowInsets(0.dp)
                    ) { innerPadding ->
                        AppNavigation(
                            navController = navController,
                            xalInitController = xalInitController,
                            modifier = Modifier
                                .padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}