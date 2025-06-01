package bruhcollective.itaysonlab.jetibox.core.models.peoplehub

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
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
    val presenceDetails: List<XblPersonPresenceDetail> = emptyList(),
    val colorTheme: String,
    val linkedAccounts: List<XblPersonLinkedAccount> = emptyList(),
    val detail: XblPersonDetail
)

@Serializable
data class XblPersonPreferredColor(
    val primaryColor: String,
    val secondaryColor: String,
    val tertiaryColor: String,
)

@Serializable
data class XblPersonLinkedAccount(
    val networkName: String,
    val displayName: String,
    val deeplink: String,
    val showOnProfile: Boolean,
    val isFamilyFriendly: Boolean,
)

@Serializable
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

@Serializable
data class XblPersonPresenceDetail(
    @SerialName(value = "Device") val device: String,
    @SerialName(value = "DeviceSubType") val deviceSubType: String? = null,
    @SerialName(value = "PresenceText") val presenceText: String,
    @SerialName(value = "State") val state: String,
    @SerialName(value = "TitleId") val titleId: String,
    @SerialName(value = "IsGame") val isGame: Boolean,
    @SerialName(value = "IsPrimary") val isPrimary: Boolean,
)