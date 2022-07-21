package bruhcollective.itaysonlab.jetibox.ui.screens.home.render

import bruhcollective.itaysonlab.jetibox.core.models.titlehub.Title
import bruhcollective.itaysonlab.jetibox.core.xbl_bridge.XblTitleDatabase

class LayoutStorage {
    val titles = mutableMapOf<Long, Title>()

    suspend fun fillTitles (
        xblTitleDatabase: XblTitleDatabase,
        list: List<Long>
    ) {
        titles.clear()
        titles.putAll(xblTitleDatabase.getTitles(list))
    }
}