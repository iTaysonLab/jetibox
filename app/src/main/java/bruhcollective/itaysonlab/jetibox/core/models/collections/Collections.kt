package bruhcollective.itaysonlab.jetibox.core.models.collections

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class QueryCollectionsBody(
    val beneficiaries: List<String>,
    val market: String,
    val includeDuplicates: Boolean,
    val checkSatisfyingEntitlements: Boolean,
    val expandSatisfyingItems: Boolean,
    val showSatisfiedBy: Boolean,
    val maxPageSize: Long, // 1000
    val validityType: String, // ValidAndFuture
    val entitlementFilters: List<String>, // *:Game
    val productSkuIds: List<ProductSkuId>,
    val continuationToken: String
)

@JsonClass(generateAdapter = true)
class QueryCollectionResponse(
    val continuationToken: String?,
    val items: List<CollectionItem>
)

@JsonClass(generateAdapter = true)
class ProductSkuId(
    val productId: String,
    val skuId: String? = null
) {
    companion object {
        val LiveGold = ProductSkuId(productId = "CFQ7TTC0K5DJ")
        val EAAccess = ProductSkuId(productId = "CFQ7TTC0K5DH")
        val Gamepass = ProductSkuId(productId = "CFQ7TTC0K6L8")
    }
}

@JsonClass(generateAdapter = true)
data class CollectionItem(
    val status: String,
    val acquiredDate: String,
    val productId: String,
    val productKind: String,
    val endDate: String,
    val startDate: String,
    val modifiedDate: String,
    val purchasedCountry: String?,
    val isTrial: Boolean?,
    val trialTimeRemaining: String?
)