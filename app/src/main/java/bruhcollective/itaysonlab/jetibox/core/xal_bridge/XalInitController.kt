package bruhcollective.itaysonlab.jetibox.core.xal_bridge

import bruhcollective.itaysonlab.jetibox.core.config.MsCapDatabase
import bruhcollective.itaysonlab.jetibox.core.xbl_bridge.XblUserController

class XalInitController(
    private val xalBridge: XalBridge,
    private val xblUserController: XblUserController,
    private val msCapDatabase: MsCapDatabase
) {
    suspend fun init(): Boolean = if (xalBridge.initialized) {
        xalBridge.currentProfile != null
    } else {
        xblUserController.tryRestoring()
        xalBridge.initialize()
        (xalBridge.tryUsingSavedData() is XalBridge.XalBridgeSemaphore.Success).also {
            if (it) { onSignIn() }
        }
    }

    suspend fun onSignIn() {
        xblUserController.reload()
        msCapDatabase.restore()
    }
}