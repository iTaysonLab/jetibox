package bruhcollective.itaysonlab.jetibox.ui.screens.home.render

import bruhcollective.itaysonlab.jetibox.core.models.titlehub.StoreBatchRequest
import bruhcollective.itaysonlab.jetibox.core.models.titlehub.Title
import bruhcollective.itaysonlab.jetibox.core.service.TitleHubService

class LayoutStorage {
    val titles = mutableMapOf<Long, Title>()

    suspend fun fillTitles (
        thService: TitleHubService,
        list: List<Long>
    ) {
        titles.clear()
        titles.putAll(thService.storeBatch(
            body = StoreBatchRequest(null, list.map { it.toString() })
        ).titles.associateBy { it.titleId })
    }
}