package bruhcollective.itaysonlab.jetibox.core.models.mediahub

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class MediaHubQuery(
    val query: String,
    val max: Int,
    val skip: Int
) {
    companion object {
        fun showFromOwner(xuid: String, max: Int, skip: Int) = MediaHubQuery(
            query = "OwnerXuid eq $xuid", max, skip
        )
    }
}