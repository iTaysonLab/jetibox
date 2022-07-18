package bruhcollective.itaysonlab.jetibox.core.xal_bridge

import com.microsoft.xalwrapper.XalApplication
import com.microsoft.xalwrapper.models.XalUser
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class XalBridge {
    var currentProfile: XalUser? = null
        private set

    var initialized = false
        private set

    var correlationVector = ""
        private set

    val currentProfileXuid get() = currentProfile?.xuid() ?: 0L

    suspend fun tryUsingSavedData() = suspendCancellableCoroutine { c ->
        XalApplication.getInstance().XalTryAddFirstUserSilently(c.asCallback())
    }

    suspend fun requestSignIn() = suspendCancellableCoroutine { c ->
        XalApplication.getInstance().XalAddUserWithUi(c.asCallback())
    }

    suspend fun initialize() = suspendCancellableCoroutine<XalBridgeSemaphore<String>> { c ->
        initialized = true
        XalApplication.getInstance().XalInitialize(
            true,
            /* isModernGamertagFlow = */ true,
            /* useMpopBehavior = */ true,
            /* useBetaServices = */ false,
            /* titleId = */ 328178078,
            /* clientId = */ "000000004c12ae6f",
            /* sandbox = */ "RETAIL",
            /* baseCorrelationVector = */ null,
            /* correlationVectorVersion = */ 0,
            /* maxLogLevel = */ XalApplication.LogLevel.IMPORTANT.value,
            /* msaRedirectUri = */ "ms-xal-000000004c12ae6f://auth",
            object : XalApplication.XalInitializeCallback {
                override fun onSuccess(str: String) {
                    correlationVector = str
                    c.resume(XalBridgeSemaphore.Success(str))
                }

                override fun onError(i2: Int, str: String) {
                    c.resume(XalBridgeSemaphore.Error(i2, str))
                }
            }
        )
    }

    fun getTokenAndSignature(url: String, forceRefresh: Boolean) = runBlocking {
        suspendCancellableCoroutine { c ->
            XalApplication.getInstance().XalUserGetTokenAndSignatureSilently(
                url,
                forceRefresh,
                object : XalApplication.XalGetTokenCallback {
                    override fun onError(i2: Int, str: String) {
                        c.resumeIfActive(null)
                    }

                    override fun onSuccess(str: String, str2: String) {
                        c.resumeIfActive(TokenAndSignature(str to str2))
                    }
                })

        }
    }

    sealed class XalBridgeSemaphore<T> {
        class Error<T>(val code: Int, val message: String) : XalBridgeSemaphore<T>()
        class Success<T>(val result: T) : XalBridgeSemaphore<T>()
    }

    private fun CancellableContinuation<XalBridgeSemaphore<XalUser>>.asCallback() =
        object : XalApplication.XalSignInCallback {
            override fun onError(i2: Int, str: String) {
                resumeIfActive(XalBridgeSemaphore.Error(i2, str))
            }

            override fun onSuccess(
                xuid: Long,
                gamertag: String?,
                uniqueModernGamertag: String?,
                ageGroup: Int,
                webAccountId: String?
            ) {
                resumeIfActive(
                    XalBridgeSemaphore.Success(
                        XalUser.withContent(
                            xuid,
                            ageGroup,
                            gamertag,
                            uniqueModernGamertag,
                            webAccountId
                        ).also { currentProfile = it })
                )
            }
        }

    private fun <T> CancellableContinuation<T>.resumeIfActive(with: T) {
        if (this.isActive) {
            resume(with)
        }
    }

    @JvmInline
    value class TokenAndSignature (private val of: Pair<String, String>) {
        val token get() = of.first
        val signature get() = of.second
    }
}