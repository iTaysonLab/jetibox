package bruhcollective.itaysonlab.jetibox.core.xal_bridge

import bruhcollective.itaysonlab.jetibox.core.config.MsCapDatabase
import bruhcollective.itaysonlab.jetibox.core.xbl_bridge.XblUserController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class XalInitController @Inject constructor(
    private val xalBridge: XalBridge,
    private val xblUserController: XblUserController,
    private val msCapDatabase: MsCapDatabase
) {
    suspend fun init(): Boolean {
        if (xalBridge.initialized) {
            return xalBridge.currentProfile != null
        } else {
            return withContext(Dispatchers.Default) {
                xblUserController.tryRestoring()
                xalBridge.initialize()
                return@withContext (xalBridge.tryUsingSavedData() is XalBridge.XalBridgeSemaphore.Success).also { success ->
                    if (success) {
                        onSignIn()
                    }
                }
            }
        }
    }

    suspend fun onSignIn() {
        xblUserController.reload()
        msCapDatabase.restore()
    }
}