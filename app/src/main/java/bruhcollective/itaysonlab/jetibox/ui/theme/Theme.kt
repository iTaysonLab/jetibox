package bruhcollective.itaysonlab.jetibox.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.ViewCompat
import bruhcollective.itaysonlab.jetibox.core.xbl_bridge.XblUserController

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

enum class ApplicationThemeSource {
    DYNAMIC, APPLICATION, XBLUSER
}

@Composable
fun JetiboxTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    themeSource: ApplicationThemeSource,
    xblUserController: XblUserController,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        themeSource == ApplicationThemeSource.DYNAMIC && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        themeSource == ApplicationThemeSource.XBLUSER -> {
            if (darkTheme) xblUserController.xblCurrentUserTheme.second else xblUserController.xblCurrentUserTheme.first
        }

        else -> if (darkTheme) DarkColorScheme else LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            ViewCompat.getWindowInsetsController(view)?.isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}