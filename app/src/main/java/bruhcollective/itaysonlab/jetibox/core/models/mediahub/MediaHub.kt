package bruhcollective.itaysonlab.jetibox.core.models.mediahub

import bruhcollective.itaysonlab.jetibox.core.models.contentbuilder.ContentBuilderLayer
import bruhcollective.itaysonlab.jetibox.core.util.TimeUtils
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Serializable
class MediaHubSearchResponse<T: MediaHubEntry>(
    val values: List<T> = emptyList(),
    val continuationToken: String? = null
)

@Serializable
class Screenshot(
    val contentId: String,
    val captureDate: String,
    val dateUploaded: String,
    val titleId: Long,
    val titleName: String,
    val resolutionWidth: Int,
    val resolutionHeight: Int,
    val ownerXuid: Long,
    val contentLocators: List<ContentLocator> = emptyList(),
    val commentCount: Int,
    val likeCount: Int,
    val shareCount: Int,
    val viewCount: Int,
): MediaHubEntry {
    override fun getEntryId() = contentId
    override fun getDate() = dateUploaded
    override fun getGameId() = titleId
    override fun getLocators() = contentLocators

    override fun formatResolution() = "${resolutionWidth}x${resolutionHeight}"
    override fun formatFilename() = "${titleName}_${TimeUtils.msDateToFilename(dateUploaded)}"
}

@Serializable
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
    val contentLocators: List<ContentLocator> = emptyList(),
    val commentCount: Int,
    val likeCount: Int,
    val shareCount: Int,
    val viewCount: Int,
): MediaHubEntry {
    override fun getEntryId() = contentId
    override fun getDate() = uploadDate
    override fun getGameId() = titleId
    override fun getLocators() = contentLocators

    override fun formatResolution() = "${resolutionWidth}x${resolutionHeight}"
    override fun formatFilename() = "${titleName}_${TimeUtils.msDateToFilename(uploadDate)}"
}

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("locatorType")
sealed class ContentLocator {
    companion object {
        val serializerModule = SerializersModule {
            polymorphic(ContentLocator::class) {
                defaultDeserializer { Unknown.serializer() }
            }
        }
    }

    abstract val uri: String

    @SerialName("Download")
    @Serializable
    class Download(
        override val uri: String, val fileSize: Long
    ): ContentLocator()

    @SerialName("Download_HDR")
    @Serializable
    class DownloadHDR(
        override val uri: String, val fileSize: Long
    ): ContentLocator()

    @SerialName("Thumbnail_Small")
    @Serializable
    class SmallThumbnail(
        override val uri: String
    ): ContentLocator()

    @SerialName("Thumbnail_Large")
    @Serializable
    class LargeThumbnail(
        override val uri: String
    ): ContentLocator()

    @Serializable
    data object Unknown: ContentLocator() {
        override val uri = ""
    }
}

interface MediaHubEntry {
    fun getEntryId(): String
    fun getDate(): String
    fun getGameId(): Long
    fun getLocators(): List<ContentLocator> = emptyList()

    fun formatResolution(): String
    fun formatFilename(): String
}