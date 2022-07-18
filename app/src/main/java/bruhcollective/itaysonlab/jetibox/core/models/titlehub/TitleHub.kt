package bruhcollective.itaysonlab.jetibox.core.models.titlehub

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class TitleHubResponse(
    val titles: List<Title>,
    val xuid: String
)

@JsonClass(generateAdapter = true)
data class Title(
    val name: String,
    val mediaItemType: String,
    val xboxLiveTier: String,
    val modernTitleId: Long,
    val titleId: Long,
    val displayImage: String,
    val devices: List<String>,
    val gamePass: TitleGamepassStatus,
    val images: List<TitleImage>?,
    val achievement: TitleAchievementInfo?,
    val detail: TitleDetail?,
    val contentBoards: List<TitleContentWarnings>?,
    val pfn: String?,
    val hardware: TitleHardwareInfo?
)

@JsonClass(generateAdapter = true)
class TitleGamepassStatus(
    val isGamePass: Boolean
)

@JsonClass(generateAdapter = true)
class TitleImage(
    val url: String,
    val type: String
)

@JsonClass(generateAdapter = true)
class TitleAchievementInfo(
    val currentAchievements: Int,
    val totalAchievements: Int,
    val currentGamerscore: Int,
    val totalGamerscore: Int,
    val progressPercentage: Float,
    val sourceVersion: Int,
)

@JsonClass(generateAdapter = true)
class TitleDetail(
    val attributes: List<TitleAttribute>,
    val description: String,
    val developerName: String,
    val genres: List<String>,
    val minAge: Int,
    val publisherName: String,
    val releaseDate: String,
    val shortDescription: String,
    val xboxLiveGoldRequired: Boolean
)

@JsonClass(generateAdapter = true)
class TitleAttribute(
    val applicablePlatforms: List<String>?,
    val name: String,
    val minimum: String?,
    val maximum: String?
)

@JsonClass(generateAdapter = true)
class TitleHardwareInfo(
    val maxDownloadSizeInBytes: Long
)

@JsonClass(generateAdapter = true)
class TitleContentWarnings(
    @Json(name = "InteractiveElements") val interactiveElements: List<String>,
    @Json(name = "RatingDescriptors") val descriptors: List<String>,
    @Json(name = "RatingDisclaimers") val disclaimers: List<String>,
    @Json(name = "RatingSystem") val ratingSystem: String,
    @Json(name = "RatingId") val ratingId: String,
)