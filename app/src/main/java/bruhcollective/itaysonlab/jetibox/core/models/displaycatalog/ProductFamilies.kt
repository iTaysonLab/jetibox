package bruhcollective.itaysonlab.jetibox.core.models.displaycatalog

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ProductFamiliesData(
    @Json(name = "DisplayData") val displayData: List<ProductFamilyGroup>
)

@JsonClass(generateAdapter = true)
class ProductFamilyGroup(
    @Json(name = "Group") val group: String,
    @Json(name = "LocalizedValues") val values: List<ProductFamilyGroupLocalizedValue>,
)

@JsonClass(generateAdapter = true)
class ProductFamilyGroupLocalizedValue(
    @Json(name = "Value") val value: String,
    @Json(name = "Localized") val localized: String
)