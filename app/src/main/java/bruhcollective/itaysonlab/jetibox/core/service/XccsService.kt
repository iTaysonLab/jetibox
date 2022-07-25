package bruhcollective.itaysonlab.jetibox.core.service

import bruhcollective.itaysonlab.jetibox.core.models.xccs.*
import retrofit2.http.*

interface XccsService {
    @GET("/lists/devices")
    @Headers("skillplatform: RemoteManagement", "x-xbl-contract-version: 4")
    suspend fun listDevices(
        @Query("queryCurrentDevice") queryCurrentDevice: Boolean = false,
        @Query("includeStorageDevices") includeStorageDevices: Boolean = true,
    ): XccsResponse<Device>

    @GET("/lists/devices")
    @Headers("skillplatform: RemoteManagement", "x-xbl-contract-version: 4")
    suspend fun listDevicesWithProducts(
        @Query("queryCurrentDevice") queryCurrentDevice: Boolean = false,
        @Query("includeStorageDevices") includeStorageDevices: Boolean = true,
        @Query("oneStoreProductIds") oneStoreProductIds: String, // "A;B"
    ): XccsResponse<DeviceAndPackages>

    @GET("/lists/installedApps")
    @Headers("skillplatform: RemoteManagement", "x-xbl-contract-version: 4")
    suspend fun listInstalledApps(
        @Query("deviceId") deviceId: String
    ): XccsResponse<InstalledApp>

    @GET("/consoles/{consoleId}/installEvents")
    @Headers("skillplatform: RemoteManagement", "x-xbl-contract-version: 4")
    suspend fun installEvents(
        @Path("consoleId") consoleId: String,
    ): XccsInstallEventsResponse

    @POST("/commands")
    @Headers("skillplatform: RemoteManagement", "x-xbl-contract-version: 4")
    suspend fun sendCommand(
        @Body command: XccsOperation
    ): XccsSendOperationResponse

    @GET("/opStatus")
    @Headers("skillplatform: RemoteManagement", "x-xbl-contract-version: 2")
    suspend fun checkOperationStatus(
        @Header("x-xbl-opid") operationId: String,
        @Header("x-xbl-deviceid") deviceId: String,
    ): XccsOperationQuery

    @PUT("/consoles/{consoleId}/name")
    @Headers("skillplatform: RemoteManagement", "x-xbl-contract-version: 4")
    suspend fun changeConsoleName(
        @Path("consoleId") consoleId: String,
        @Body body: Map<String, String>
    ): XccsStatus
}