package bruhcollective.itaysonlab.jetibox.core.xbl_bridge

import bruhcollective.itaysonlab.jetibox.core.service.XccsService

class XccsController(
    private val xccsService: XccsService
) {
    suspend fun installedApps(consoleId: String) = xccsService.listInstalledApps(consoleId).result
    suspend fun renameConsole(consoleId: String, newName: String) = xccsService.changeConsoleName(consoleId, body = mapOf("name" to newName))
}