package bruhcollective.itaysonlab.jetibox.core.models.mediahub

import com.squareup.moshi.JsonClass
import dev.zacsweers.moshix.sealed.annotations.DefaultObject
import dev.zacsweers.moshix.sealed.annotations.TypeLabel

@JsonClass(generateAdapter = true)
class MediaHubSearchResponse<T: MediaHubEntry>(
    val values: List<T>,
    val continuationToken: String?
)

@JsonClass(generateAdapter = true)
class Screenshot(
    val contentId: String,
    val captureDate: String,
    val dateUploaded: String,
    val titleId: Long,
    val titleName: String,
    val resolutionWidth: Int,
    val resolutionHeight: Int,
    val ownerXuid: Long,
    val contentLocators: List<ContentLocator>,
    val commentCount: Int,
    val likeCount: Int,
    val shareCount: Int,
    val viewCount: Int,
): MediaHubEntry {
    override fun getEntryId() = contentId
    override fun getDate() = dateUploaded
    override fun getGameId() = titleId
    override fun getLocators() = contentLocators
}

@JsonClass(generateAdapter = true)
class Gameclip(
    val contentId: String,
    val uploadDate: String,

    val resolutionWidth: Int,
    val resolutionHeight: Int,
    val durationInSeconds: Int,
    val frameRate: Int,

    val titleId: Long,
    val titleName: String,
    val ownerXuid: Long,
    val contentLocators: List<ContentLocator>,
    val commentCount: Int,
    val likeCount: Int,
    val shareCount: Int,
    val viewCount: Int,
): MediaHubEntry {
    override fun getEntryId() = contentId
    override fun getDate() = uploadDate
    override fun getGameId() = titleId
    override fun getLocators() = contentLocators
}

@JsonClass(generateAdapter = true, generator = "sealed:locatorType")
sealed class ContentLocator {
    abstract val uri: String

    @TypeLabel("Download")
    @JsonClass(generateAdapter = true)
    class Download(
        override val uri: String, val fileSize: Long
    ): ContentLocator()

    @TypeLabel("Download_HDR")
    @JsonClass(generateAdapter = true)
    class DownloadHDR(
        override val uri: String, val fileSize: Long
    ): ContentLocator()

    @TypeLabel("Thumbnail_Small")
    @JsonClass(generateAdapter = true)
    class SmallThumbnail(
        override val uri: String
    ): ContentLocator()

    @TypeLabel("Thumbnail_Large")
    @JsonClass(generateAdapter = true)
    class LargeThumbnail(
        override val uri: String
    ): ContentLocator()

    @DefaultObject
    object Unknown: ContentLocator() {
        override val uri = ""
    }
}

interface MediaHubEntry {
    fun getEntryId(): String
    fun getDate(): String
    fun getGameId(): Long
    fun getLocators(): List<ContentLocator>
}