package bruhcollective.itaysonlab.jetibox.core.models.mediahub

import kotlinx.serialization.Serializable

@Serializable
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