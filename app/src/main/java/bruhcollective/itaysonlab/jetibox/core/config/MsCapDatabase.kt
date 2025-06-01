package bruhcollective.itaysonlab.jetibox.core.config

import bruhcollective.itaysonlab.jetibox.core.models.displaycatalog.ProductFamiliesData
import bruhcollective.itaysonlab.jetibox.core.models.displaycatalog.ProductFamilyGroup
import bruhcollective.itaysonlab.jetibox.core.models.displaycatalog.RatingBoard
import bruhcollective.itaysonlab.jetibox.core.models.displaycatalog.RatingBoardsData
import bruhcollective.itaysonlab.jetibox.core.service.DisplayCatalogService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalStdlibApi::class)
@Singleton
class MsCapDatabase @Inject constructor(
    private val cfgService: ConfigService,
    private val displayCatalogService: DisplayCatalogService,
    private val json: Json
) {
    private var mObjPFamilies: List<ProductFamilyGroup> = emptyList()

    var mObjPFCaps: Map<String, String> = emptyMap()
    private set

    var mObjRatings: Map<String, RatingBoard> = emptyMap()
        private set

    suspend fun restore() = withContext(Dispatchers.IO) {
        mObjRatings = restoreOrFetch("msdb.ratings", RatingBoardsData.serializer(), displayCatalogService::getRatings)?.ratingBoards.orEmpty()
        mObjPFamilies = restoreOrFetch("msdb.productfamilies", ProductFamiliesData.serializer(), displayCatalogService::getProductFamiliesForGames)?.displayData.orEmpty()
        mObjPFCaps = mObjPFamilies.first { it.group == "StoreClientSharedStrings" }.values.associate { it.value to it.localized }
    }

    private suspend fun <T> restoreOrFetch (key: String, adapter: KSerializer<T>, funcToFetch: suspend (market: String, languages: String) -> T): T? {
        val fetch = suspend {
            funcToFetch(cfgService.marketCountry, cfgService.marketLanguage).also {
                cfgService.put(key, json.encodeToString(adapter, it))
            }
        }

        return if (cfgService.has(key)) {
            try {
                json.decodeFromString(adapter, cfgService.string(key, ""))
            } catch (e: Exception) {
                // malformed JSON or invalid fields
                fetch()
            }
        } else {
            fetch()
        }
    }
}