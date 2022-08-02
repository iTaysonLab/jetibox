package bruhcollective.itaysonlab.jetibox.core.xbl_bridge

import android.content.Context
import androidx.room.Room
import bruhcollective.itaysonlab.jetibox.core.config.ConfigService
import bruhcollective.itaysonlab.jetibox.core.ext.debugLog
import bruhcollective.itaysonlab.jetibox.core.models.collections.CollectionItem
import bruhcollective.itaysonlab.jetibox.core.models.collections.QueryCollectionsBody
import bruhcollective.itaysonlab.jetibox.core.models.titlehub.Title
import bruhcollective.itaysonlab.jetibox.core.room.JBDatabase
import bruhcollective.itaysonlab.jetibox.core.room.models.RoomCollectionItem
import bruhcollective.itaysonlab.jetibox.core.service.CollectionsService
import bruhcollective.itaysonlab.jetibox.core.service.DisplayCatalogService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.days

@Singleton
class XblCollectionController @Inject constructor(
    @ApplicationContext private val context: Context,
    private val configService: ConfigService,
    private val titleDatabase: XblTitleDatabase,
    private val displayCatalogService: DisplayCatalogService,
    private val collectionsService: CollectionsService
) {
    companion object {
        private val REFRESH_RATE = 1.days
    }

    private val database = Room.databaseBuilder(context, JBDatabase::class.java, "jb_db").build()

    suspend fun getUserCollection(forceRefresh: Boolean = false): List<CollectionItem> {
        return if (forceRefresh || checkDate()) {
            fetchCollectionFromNetwork()
        } else {
            fetchCollectionFromDatabase()
        }
    }

    private suspend fun fetchCollectionFromDatabase(): List<CollectionItem> {
        val roomItems = database.collectionDao().getAll()
        val titles = titleDatabase.getTitles(roomItems.map { it.titleId })
        return roomItems.mapNotNull { CollectionItem(it to (titles[it.titleId] ?: return@mapNotNull null)) }
    }

    private suspend fun fetchCollectionFromNetwork(): List<CollectionItem> {
        val items = mutableListOf<bruhcollective.itaysonlab.jetibox.core.models.collections.CollectionItem>()
        var token = ""

        while (true) {
            val response = collectionsService.query(QueryCollectionsBody(
                beneficiaries = emptyList(),
                market = configService.marketCountry,
                includeDuplicates = false,
                checkSatisfyingEntitlements = true,
                expandSatisfyingItems = true,
                showSatisfiedBy = true,
                maxPageSize = 1000,
                validityType = "ValidAndFuture",
                entitlementFilters = listOf("*:Game"),
                productSkuIds = emptyList(),
                continuationToken = token
            ))

            items += response.items

            if (response.continuationToken.isNullOrEmpty()) {
                break
            } else {
                token = response.continuationToken
            }
        }

        // it only begins...

        val products = displayCatalogService.getProducts(
            items.joinToString(",") { it.productId },
            configService.marketCountry,
            configService.marketLanguage
        ).products
            .associate { it.productId to it.xboxTitleId }
            .filterNot { it.value == 0L }

        val roomItems = items.mapNotNull { item ->
            return@mapNotNull if (products.containsKey(item.productId)) {
                RoomCollectionItem(
                    productId = item.productId,
                    acquiredDate = Instant.parse(item.acquiredDate).epochSeconds,
                    startDate = Instant.parse(item.startDate).epochSeconds,
                    modifiedDate = Instant.parse(item.modifiedDate).epochSeconds,
                    endDate = Instant.parse(item.endDate).epochSeconds,
                    purchasedCountry = item.purchasedCountry ?: "",
                    productKind = item.productKind,
                    status = item.status,
                    isTrial = item.isTrial ?: false,
                    trialTimeRemaining = item.trialTimeRemaining ?: "",
                    titleId = products[item.productId]!!
                )
            } else {
                null
            }
        }

        database.collectionDao().insertAll(*roomItems.toTypedArray())
        configService.collectionLastUpdate = Clock.System.now().epochSeconds

        val titles = titleDatabase.getTitles(roomItems.map { it.titleId })

        return roomItems.mapNotNull { CollectionItem(it to (titles[it.titleId] ?: return@mapNotNull null)) }
    }

    private fun checkDate(): Boolean {
        return configService.collectionLastUpdate + REFRESH_RATE.inWholeMilliseconds < Clock.System.now().epochSeconds
    }

    @JvmInline
    value class CollectionItem (private val of: Pair<RoomCollectionItem, Title>) {
        val item get() = of.first
        val title get() = of.second
    }
}