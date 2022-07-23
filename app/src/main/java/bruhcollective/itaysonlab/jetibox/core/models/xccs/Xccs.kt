package bruhcollective.itaysonlab.jetibox.core.models.xccs

import com.squareup.moshi.JsonClass
import dev.zacsweers.moshix.sealed.annotations.TypeLabel

@JsonClass(generateAdapter = true)
class XccsResponse <T> (
    val status: XccsStatus,
    val result: List<T>
)

@JsonClass(generateAdapter = true)
class XccsStatus (
    val errorCode: String,
    val errorMessage: String?,
)

//

@JsonClass(generateAdapter = true)
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
    val storageDevices: List<StorageDevice>
)

@JsonClass(generateAdapter = true)
data class StorageDevice(
    val storageDeviceId: String,
    val storageDeviceName: String,
    val isDefault: Boolean,
    val freeSpaceBytes: Long,
    val totalSpaceBytes: Long,
    val isGen9Compatible: Boolean?
)

enum class DevicePowerState {
    ConnectedStandby, On, Off
}

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

@JsonClass(generateAdapter = true)
data class InstalledApp(
    val oneStoreProductId: String,
    val titleId: Long,
    val aumid: String?,
    val isGame: Boolean,
    val name: String,
    val contentType: String,
    val instanceId: String,
    val storageDeviceId: String,
    val uniqueId: String,
    val version: Long,
    val sizeInBytes: Long,
    val installTime: String,
    val updateTime: String?,
    val lastActiveTime: String?,
    val parentId: String?,
    val legacyProductId: String?
)

//

@JsonClass(generateAdapter = true)
data class XccsOperation(
    val destination: String, // Xbox
    val type: String, // Shell
    val command: String, // UninstallPackage / InstallPackages
    val sessionId: String, // maybe UUID generation?
    val sourceId: String, // com.microsoft.xboxone.smartglass
    val parameters: List<Map<String, String>>, // first: instanceId with installedApp.instanceId (or bigCatIdList if installing), second: installEvents with true
    val linkedXboxId: String // logic
)

@JsonClass(generateAdapter = true)
class XccsSendOperationResponse (
    val status: XccsStatus,
    val opId: String
)

@JsonClass(generateAdapter = true)
class XccsOperationQuery(
    val status: XccsStatus,
    val operationStatus: String,
    val opId: String,
    val succeeded: Boolean,
    val command: String
)

//

@JsonClass(generateAdapter = true)
class XccsInstallEventsResponse (
    val status: XccsStatus,
    val result: List<InstallEvent>
)

@JsonClass(generateAdapter = true, generator = "sealed:eventType")
sealed class InstallEvent {
    @TypeLabel("InstallStarted")
    @JsonClass(generateAdapter = true)
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

    @TypeLabel("UninstallCompleted")
    @JsonClass(generateAdapter = true)
    class UninstallCompleted(
        val eventTime: String,
        val oneStoreProductId: String,
        val instanceId: String,
        val titleId: Long,
        val productName: String,
    ): InstallEvent()
}