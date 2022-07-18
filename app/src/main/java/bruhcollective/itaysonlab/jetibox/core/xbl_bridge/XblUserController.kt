package bruhcollective.itaysonlab.jetibox.core.xbl_bridge

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.graphics.toColorInt
import bruhcollective.itaysonlab.jetibox.core.config.ConfigService
import bruhcollective.itaysonlab.jetibox.core.models.peoplehub.XblPerson
import bruhcollective.itaysonlab.jetibox.core.service.PeopleHubService
import bruhcollective.itaysonlab.jetibox.core.xal_bridge.XalBridge
import bruhcollective.itaysonlab.jetibox.ui.monet.ColorToScheme
import javax.inject.Inject

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

        configService.put("xbl.user.color", xblCurrentUserNotNull.preferredColor.primaryColor)

        xblCurrentUserAvailable = true
    }

    suspend fun tryRestoring() {
        if (configService.has("xbl.user.color") && configService.string("xbl.user.color", "").isNotEmpty()) {
            xblCurrentUserTheme = generatePairOf("#${configService.string("xbl.user.color", "")}".toColorInt())
        }
    }

    fun reset() {
        xblCurrentUserAvailable = false
        xblCurrentUserAvatar = ""
        xblCurrentUserDisplayName = ""
        xblCurrentUserPreferredColor = 0
    }

    private suspend fun generatePairOf(color: Int) = ColorToScheme.convert(color, false) to ColorToScheme.convert(color, true)
}