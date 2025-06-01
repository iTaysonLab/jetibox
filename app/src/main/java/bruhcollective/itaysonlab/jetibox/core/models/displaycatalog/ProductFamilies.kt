package bruhcollective.itaysonlab.jetibox.core.models.displaycatalog

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ProductFamiliesData(
    @SerialName(value = "DisplayData") val displayData: List<ProductFamilyGroup> = emptyList()
)

@Serializable
class ProductFamilyGroup(
    @SerialName(value = "Group") val group: String,
    @SerialName(value = "LocalizedValues") val values: List<ProductFamilyGroupLocalizedValue> = emptyList(),
)

@Serializable
class ProductFamilyGroupLocalizedValue(
    @SerialName(value = "Value") val value: String,
    @SerialName(value = "Localized") val localized: String
)