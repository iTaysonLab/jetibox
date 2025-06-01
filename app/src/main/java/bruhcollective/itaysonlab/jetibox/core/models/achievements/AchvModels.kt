package bruhcollective.itaysonlab.jetibox.core.models.achievements

import kotlinx.serialization.Serializable

@Serializable
class AchievementsResponse(
    val achievements: List<Achievement> = emptyList(),
    val pagingInfo: AchievementPagingInfo
)

@Serializable
class AchievementPagingInfo(
    val totalRecords: Int,
    val continuationToken: String? = null,
)

@Serializable
class Achievement(
    val name: String,
    val lockedDescription: String,
    val description: String,
    val isSecret: Boolean,
    val rarity: AchievementRarity,
    val rewards: List<AchievementReward> = emptyList(),
    val mediaAssets: List<AchievementMedia> = emptyList(),
)

@Serializable
class AchievementRarity(
    val currentPercentage: Float,
    val currentCategory: String
)

@Serializable
class AchievementReward(
    val value: Int,
    val type: String
)

@Serializable
class AchievementMedia(
    val name: String,
    val type: String,
    val url: String,
)