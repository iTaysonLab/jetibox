package bruhcollective.itaysonlab.jetibox.core.models.peoplehub

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class XblPerson(
    val xuid: String,
    val displayName: String,
    val realName: String,
    val displayPicRaw: String,
    val gamertag: String,
    val gamerScore: String,
    val modernGamertag: String,
    val modernGamertagSuffix: String,
    val uniqueModernGamertag: String,
    val xboxOneRep: String,
    val presenceState: String,
    val presenceText: String,
    val isXbox360Gamerpic: Boolean,
    val preferredColor: XblPersonPreferredColor,
    val presenceDetails: List<XblPersonPresenceDetail>,
    val colorTheme: String,
    val linkedAccounts: List<XblPersonLinkedAccount>,
    val detail: XblPersonDetail
)

@JsonClass(generateAdapter = true)
data class XblPersonPreferredColor(
    val primaryColor: String,
    val secondaryColor: String,
    val tertiaryColor: String,
)

@JsonClass(generateAdapter = true)
data class XblPersonLinkedAccount(
    val networkName: String,
    val displayName: String,
    val deeplink: String,
    val showOnProfile: Boolean,
    val isFamilyFriendly: Boolean,
)

@JsonClass(generateAdapter = true)
data class XblPersonDetail(
    val accountTier: String,
    val bio: String,
    val isVerified: Boolean,
    val mute: Boolean,
    val blocked: Boolean,
    val hasGamePass: Boolean,
    val followingCount: Int,
    val followerCount: Int,
) {
    @Transient val isGold = accountTier == "Gold"
}

@JsonClass(generateAdapter = true)
data class XblPersonPresenceDetail(
    @Json(name = "Device") val device: String,
    @Json(name = "DeviceSubType") val deviceSubType: String?,
    @Json(name = "PresenceText") val presenceText: String,
    @Json(name = "State") val state: String,
    @Json(name = "TitleId") val titleId: String,
    @Json(name = "IsGame") val isGame: Boolean,
    @Json(name = "IsPrimary") val isPrimary: Boolean,
)