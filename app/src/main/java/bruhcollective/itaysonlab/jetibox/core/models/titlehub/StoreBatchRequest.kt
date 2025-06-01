package bruhcollective.itaysonlab.jetibox.core.models.titlehub

import kotlinx.serialization.Serializable

@Serializable
class StoreBatchRequest(
    val pfns: String? = null,
    val titleIds: List<String> = emptyList()
)