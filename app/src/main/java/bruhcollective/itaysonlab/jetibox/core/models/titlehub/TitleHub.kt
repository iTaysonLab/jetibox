package bruhcollective.itaysonlab.jetibox.core.models.titlehub

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
class TitleHubResponse(
    val titles: List<Title> = emptyList(),
    val xuid: String
)

@Serializable
data class Title(
    val name: String,
    val mediaItemType: String,
    val xboxLiveTier: String,
    val modernTitleId: Long,
    val titleId: Long,
    val displayImage: String,
    val devices: List<String> = emptyList(),
    val gamePass: TitleGamepassStatus,
    val images: List<TitleImage> = emptyList(),
    val achievement: TitleAchievementInfo? = null,
    val detail: TitleDetail? = null,
    val contentBoards: List<TitleContentWarnings> = emptyList(),
    val pfn: String? = null,
    val hardware: TitleHardwareInfo? = null,
    val productId: String? = null,
    val productIds: List<String> = emptyList()
)

@Serializable
class TitleGamepassStatus(
    val isGamePass: Boolean
)

@Serializable
class TitleImage(
    val url: String,
    val type: String
)

@Serializable
class TitleAchievementInfo(
    val currentAchievements: Int,
    val totalAchievements: Int,
    val currentGamerscore: Int,
    val totalGamerscore: Int,
    val progressPercentage: Float,
    val sourceVersion: Int,
)

@Serializable
class TitleDetail(
    val attributes: List<TitleAttribute> = emptyList(),
    val description: String,
    val developerName: String,
    val genres: List<String> = emptyList(),
    val minAge: Int,
    val publisherName: String,
    val releaseDate: String,
    val shortDescription: String,
    val xboxLiveGoldRequired: Boolean
)

@Serializable
class TitleAttribute(
    val applicablePlatforms: List<String> = emptyList(),
    val name: String,
    val minimum: Int? = null,
    val maximum: Int? = null
)

@Serializable
class TitleHardwareInfo(
    val maxDownloadSizeInBytes: Long
)

@Serializable
class TitleContentWarnings(
    @SerialName(value = "InteractiveElements") val interactiveElements: List<String> = emptyList(),
    @SerialName(value = "RatingDescriptors") val descriptors: List<String> = emptyList(),
    @SerialName(value = "RatingDisclaimers") val disclaimers: List<String> = emptyList(),
    @SerialName(value = "RatingSystem") val ratingSystem: String,
    @SerialName(value = "RatingId") val ratingId: String,
)