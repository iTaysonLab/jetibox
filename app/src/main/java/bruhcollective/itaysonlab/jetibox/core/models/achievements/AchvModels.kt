package bruhcollective.itaysonlab.jetibox.core.models.achievements

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class AchievementsResponse(
    val achievements: List<Achievement>,
    val pagingInfo: AchievementPagingInfo
)

@JsonClass(generateAdapter = true)
class AchievementPagingInfo(
    val totalRecords: Int,
    val continuationToken: String?,
)

@JsonClass(generateAdapter = true)
class Achievement(
    val name: String,
    val lockedDescription: String,
    val description: String,
    val isSecret: Boolean,
    val rarity: AchievementRarity,
    val rewards: List<AchievementReward>,
    val mediaAssets: List<AchievementMedia>,
)

@JsonClass(generateAdapter = true)
class AchievementRarity(
    val currentPercentage: Float,
    val currentCategory: String
)

@JsonClass(generateAdapter = true)
class AchievementReward(
    val value: Int,
    val type: String
)

@JsonClass(generateAdapter = true)
class AchievementMedia(
    val name: String,
    val type: String,
    val url: String,
)