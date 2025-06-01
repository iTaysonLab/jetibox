package bruhcollective.itaysonlab.jetibox.core.models.xccs

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class XccsResponse <T: XccsResponseModel> (
    val status: XccsStatus,
    val result: List<T> = emptyList()
)

@Serializable
class XccsStatus (
    val errorCode: String,
    val errorMessage: String? = null,
)

//

@Serializable
class DeviceAndPackages(
    val device: Device,
    val packages: List<InstalledApp> = emptyList()
): XccsResponseModel

@Serializable
data class Device(
    val id: String,
    val name: String,
    val locale: String,
    val region: String,
    val consoleType: ConsoleType,
    val powerState: DevicePowerState,
    val digitalAssistantRemoteControlEnabled: Boolean,
    val remoteManagementEnabled: Boolean,
    val consoleStreamingEnabled: Boolean,
    val wirelessWarning: Boolean,
    val outOfHomeWarning: Boolean,
    val storageDevices: List<StorageDevice> = emptyList()
): XccsResponseModel

@Serializable
data class StorageDevice(
    val storageDeviceId: String,
    val storageDeviceName: String,
    val isDefault: Boolean,
    val freeSpaceBytes: Long,
    val totalSpaceBytes: Long,
    val isGen9Compatible: Boolean?
)

@Serializable
enum class DevicePowerState {
    ConnectedStandby, On, Off
}

@Serializable
enum class ConsoleType(
    val productName: String
) {
    XboxSeriesX("Xbox Series X"),
    XboxSeriesS("Xbox Series S"),
    XboxOneX("Xbox One X"),
    XboxOneSDigital("Xbox One S All-Digital"),
    XboxOneS("Xbox One S"),
    XboxOne("Xbox One")
}

//

@Serializable
data class InstalledApp(
    val oneStoreProductId: String? = null,
    val titleId: Long,
    val aumid: String? = null,
    val isGame: Boolean,
    val name: String,
    val contentType: String,
    val instanceId: String,
    val storageDeviceId: String,
    val uniqueId: String,
    val version: Long,
    val sizeInBytes: Long,
    val installTime: String,
    val updateTime: String? = null,
    val lastActiveTime: String? = null,
    val parentId: String? = null,
    val legacyProductId: String? = null
): XccsResponseModel

//

@Serializable
data class XccsOperation(
    val destination: String, // Xbox
    val type: String, // Shell
    val command: String, // UninstallPackage / InstallPackages
    val sessionId: String, // maybe UUID generation?
    val sourceId: String, // com.microsoft.xboxone.smartglass
    val parameters: List<Map<String, String>> = emptyList(), // first: instanceId with installedApp.instanceId (or bigCatIdList if installing), second: installEvents with true
    val linkedXboxId: String // logic
)

@Serializable
class XccsSendOperationResponse (
    val status: XccsStatus,
    val opId: String
)

@Serializable
class XccsOperationQuery(
    val status: XccsStatus,
    val operationStatus: String,
    val opId: String,
    val succeeded: Boolean,
    val command: String
)

//

@Serializable
class XccsInstallEventsResponse (
    val status: XccsStatus,
    val result: List<InstallEvent> = emptyList()
)

@Serializable
sealed class InstallEvent {
    @SerialName("InstallStarted")
    @Serializable
    class InstallStarted(
        val eventTime: String,
        val oneStoreProductId: String,
        val instanceId: String,
        val titleId: Long,
        val productName: String,
        val totalBytes: Long,
        val streamedBytes: Long,
        val readyToPlayBytes: Long,
        val streamedReadyToPlayBytes: Long,
        val transferState: String
    ): InstallEvent()

    @SerialName("UninstallCompleted")
    @Serializable
    class UninstallCompleted(
        val eventTime: String,
        val oneStoreProductId: String,
        val instanceId: String,
        val titleId: Long,
        val productName: String,
    ): InstallEvent()
}

@Serializable
@Keep
sealed interface XccsResponseModel