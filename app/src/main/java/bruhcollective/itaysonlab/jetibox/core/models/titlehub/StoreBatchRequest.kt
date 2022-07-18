package bruhcollective.itaysonlab.jetibox.core.models.titlehub

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class StoreBatchRequest(
    val pfns: String? = null,
    val titleIds: List<String>
)