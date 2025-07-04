package bruhcollective.itaysonlab.jetibox.core.xbl_bridge

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import bruhcollective.itaysonlab.jetibox.core.config.ConfigService
import bruhcollective.itaysonlab.jetibox.core.models.peoplehub.XblPerson
import bruhcollective.itaysonlab.jetibox.core.service.PeopleHubService
import bruhcollective.itaysonlab.jetibox.core.xal_bridge.XalBridge
import com.materialkolor.ktx.DynamicScheme
import com.materialkolor.toColorScheme
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class XblUserController @Inject constructor(
    private val xalBridge: XalBridge,
    private val phApi: PeopleHubService,
    private val configService: ConfigService
) {
    var xblCurrentUser: XblPerson? = null
    private set

    val xblCurrentUserNotNull get() = xblCurrentUser!!

    // states

    var xblCurrentUserAvailable by mutableStateOf(false)
        private set

    var xblCurrentUserAvatar by mutableStateOf("")
        private set

    var xblCurrentUserDisplayName by mutableStateOf("")
        private set

    var xblCurrentUserPreferredColor by mutableStateOf(0)
        private set

    // states / theme

    var xblCurrentUserTheme by mutableStateOf(lightColorScheme() to darkColorScheme())

    suspend fun reload() {
        xblCurrentUser = phApi.getPeople(xalBridge.currentProfileXuid.toString()).people.first()

        xblCurrentUserAvatar = xblCurrentUserNotNull.displayPicRaw
        xblCurrentUserDisplayName = xblCurrentUserNotNull.modernGamertag

        xblCurrentUserPreferredColor = "#${xblCurrentUserNotNull.preferredColor.primaryColor}".toColorInt()
        xblCurrentUserTheme = generatePairOf(xblCurrentUserPreferredColor)

        configService.userColor = xblCurrentUserNotNull.preferredColor.primaryColor

        xblCurrentUserAvailable = true
    }

    fun tryRestoring() {
        if (configService.userColor.isNotEmpty()) {
            xblCurrentUserTheme = generatePairOf("#${configService.userColor}".toColorInt())
        }
    }

    fun reset() {
        xblCurrentUserAvailable = false
        xblCurrentUserAvatar = ""
        xblCurrentUserDisplayName = ""
        xblCurrentUserPreferredColor = 0
    }

    private fun generatePairOf(color: Int): Pair<ColorScheme, ColorScheme> {
        return Color(color).let { composeColor ->
            val lightDs = DynamicScheme(seedColor = composeColor, isDark = false)
            val darkDs = DynamicScheme(seedColor = composeColor, isDark = true)

            lightDs.toColorScheme() to darkDs.toColorScheme()
        }
    }
}