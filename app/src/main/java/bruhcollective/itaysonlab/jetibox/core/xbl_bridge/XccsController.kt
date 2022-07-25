package bruhcollective.itaysonlab.jetibox.core.xbl_bridge

import bruhcollective.itaysonlab.jetibox.core.models.xccs.XccsOperation
import bruhcollective.itaysonlab.jetibox.core.service.XccsService
import kotlinx.coroutines.delay
import java.util.*

class XccsController(
    private val xccsService: XccsService
) {
    private val session = UUID.randomUUID().toString()

    suspend fun consoles() = xccsService.listDevices(queryCurrentDevice = false, includeStorageDevices = true).result
    suspend fun installedApps(consoleId: String) = xccsService.listInstalledApps(consoleId).result
    suspend fun renameConsole(consoleId: String, newName: String) = xccsService.changeConsoleName(consoleId, body = mapOf("name" to newName))
    suspend fun installedOnAnyDevice(packages: List<String>) = xccsService.listDevicesWithProducts(oneStoreProductIds = packages.joinToString(";")).result.isNotEmpty()

    suspend fun deleteApplication(device: String, instance: String) = executeAndWait(device, XccsOperation(
        destination = "Xbox",
        type = "Shell",
        command = "UninstallPackage",
        sessionId = session,
        sourceId = "com.microsoft.xboxone.smartglass",
        linkedXboxId = device,
        parameters = listOf(mapOf("instanceId" to decorateInstance(instance)), mapOf("installEvents" to "true"))
    ))

    //

    private fun decorateInstance(instance: String) = instance.split("#").let { "${it[0]}#{${it[1]}}" }

    private suspend fun executeAndWait(
        onDevice: String,
        command: XccsOperation
    ): ExecutionResult {
        return try {
            val dispatchCommandResult = xccsService.sendCommand(command)

            while (true) {
                val opStatus = xccsService.checkOperationStatus(operationId = dispatchCommandResult.opId, deviceId = onDevice)

                if (opStatus.operationStatus == "Error") {
                    return ExecutionResult.XccsError
                } else if (opStatus.succeeded) {
                    break
                }

                delay(1000L)
            }

            ExecutionResult.Success
        } catch (e: Exception) {
            ExecutionResult.Error(e)
        }
    }

    sealed class ExecutionResult {
        object Success: ExecutionResult()
        object XccsError: ExecutionResult()

        class Error(val e: Exception): ExecutionResult()
    }
}