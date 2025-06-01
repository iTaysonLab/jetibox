package bruhcollective.itaysonlab.jetibox.core.models.collections

import kotlinx.serialization.Serializable

@Serializable
class QueryCollectionsBody(
    val beneficiaries: List<String> = emptyList(),
    val market: String,
    val includeDuplicates: Boolean,
    val checkSatisfyingEntitlements: Boolean,
    val expandSatisfyingItems: Boolean,
    val showSatisfiedBy: Boolean,
    val maxPageSize: Long, // 1000
    val validityType: String, // ValidAndFuture
    val entitlementFilters: List<String> = emptyList(), // *:Game
    val productSkuIds: List<ProductSkuId> = emptyList(),
    val continuationToken: String
)

@Serializable
class QueryCollectionResponse(
    val continuationToken: String? = null,
    val items: List<CollectionItem> = emptyList()
)

@Serializable
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

@Serializable
data class CollectionItem(
    val status: String,
    val acquiredDate: String,
    val productId: String,
    val productKind: String,
    val endDate: String,
    val startDate: String,
    val modifiedDate: String,
    val purchasedCountry: String? = null,
    val isTrial: Boolean? = null,
    val trialTimeRemaining: String? = null
)