package bruhcollective.itaysonlab.jetibox.core.config

import bruhcollective.itaysonlab.jetibox.core.models.displaycatalog.ProductFamiliesData
import bruhcollective.itaysonlab.jetibox.core.models.displaycatalog.ProductFamilyGroup
import bruhcollective.itaysonlab.jetibox.core.models.displaycatalog.RatingBoard
import bruhcollective.itaysonlab.jetibox.core.models.displaycatalog.RatingBoardsData
import bruhcollective.itaysonlab.jetibox.core.service.DisplayCatalogService
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonEncodingException
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalStdlibApi::class)
@Singleton
class MsCapDatabase @Inject constructor(
    private val cfgService: ConfigService,
    private val displayCatalogService: DisplayCatalogService,
    private val moshi: Moshi
) {
    private val ratingAdapter = moshi.adapter<RatingBoardsData>()
    private val pfAdapter = moshi.adapter<ProductFamiliesData>()

    private var mObjPFamilies: List<ProductFamilyGroup> = emptyList()

    var mObjPFCaps: Map<String, String> = emptyMap()
    private set

    var mObjRatings: Map<String, RatingBoard> = emptyMap()
        private set

    suspend fun restore() = withContext(Dispatchers.IO) {
        mObjRatings = restoreOrFetch("msdb.ratings", ratingAdapter, displayCatalogService::getRatings)?.ratingBoards.orEmpty()
        mObjPFamilies = restoreOrFetch("msdb.productfamilies", pfAdapter, displayCatalogService::getProductFamiliesForGames)?.displayData.orEmpty()
        mObjPFCaps = mObjPFamilies.first { it.group == "StoreClientSharedStrings" }.values.associate { it.value to it.localized }
    }

    private suspend fun <T> restoreOrFetch (key: String, adapter: JsonAdapter<T>, funcToFetch: suspend (market: String, languages: String) -> T): T? {
        val fetch = suspend {
            funcToFetch(cfgService.marketCountry, cfgService.marketLanguage).also {
                cfgService.put(key, adapter.toJson(it))
            }
        }

        return if (cfgService.has(key)) {
            try {
                adapter.fromJson(cfgService.string(key, ""))
            } catch (e: Exception) {
                // malformed JSON or invalid fields
                fetch()
            }
        } else {
            fetch()
        }
    }
}