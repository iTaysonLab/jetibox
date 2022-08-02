package bruhcollective.itaysonlab.jetibox.core.models.displaycatalog

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class ProductsData(
    @Json(name = "Products") val products: List<Product>
)

@JsonClass(generateAdapter = true)
class Product(
    @Json(name = "AlternateIds") val alternativeIds: List<AlternativeId>,
    @Json(name = "ProductId") val productId: String
) {
    val xboxTitleId get() = alternativeIds.firstOrNull() { it.idType == "XboxTitleId" }?.value?.toLong() ?: 0L
}

@JsonClass(generateAdapter = true)
class AlternativeId(
    @Json(name = "Value") val value: String,
    @Json(name = "IdType") val idType: String
)