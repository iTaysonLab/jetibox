package bruhcollective.itaysonlab.jetibox.core.models.displaycatalog

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ProductsData(
    @SerialName(value = "Products") val products: List<Product> = emptyList()
)

@Serializable
class Product(
    @SerialName(value = "AlternateIds") val alternativeIds: List<AlternativeId> = emptyList(),
    @SerialName(value = "ProductId") val productId: String
) {
    val xboxTitleId get() = alternativeIds.firstOrNull() { it.idType == "XboxTitleId" }?.value?.toLong() ?: 0L
}

@Serializable
class AlternativeId(
    @SerialName(value = "Value") val value: String,
    @SerialName(value = "IdType") val idType: String
)