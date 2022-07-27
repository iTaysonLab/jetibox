package bruhcollective.itaysonlab.jetibox.core.xbl_bridge

import bruhcollective.itaysonlab.jetibox.core.config.ConfigService
import bruhcollective.itaysonlab.jetibox.core.ext.debugLog
import bruhcollective.itaysonlab.jetibox.core.models.titlehub.StoreBatchRequest
import bruhcollective.itaysonlab.jetibox.core.models.titlehub.Title
import bruhcollective.itaysonlab.jetibox.core.service.TitleHubService
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class XblTitleDatabase @Inject constructor(
    private val database: ConfigService,
    private val moshi: Moshi,
    private val service: TitleHubService
) {
    companion object {
        private const val TAG = "XblTitleDatabase"
    }

    @OptIn(ExperimentalStdlibApi::class) private val titleAdapter by lazy { moshi.adapter<Title>() }

    suspend fun getTitles(titles: List<Long>) = withContext(Dispatchers.IO) {
        val sepTitles = titles.partition { id -> database.has("titles.$id") }

        debugLog(TAG, "[getTitles] local: ${sepTitles.first.size}, network: ${sepTitles.second.size}")

        val localFetched = sepTitles.first
            .mapNotNull { id ->
                titleAdapter.fromJson(database.string("titles.$id", ""))
            }

        if (sepTitles.second.isEmpty()) {
            return@withContext localFetched.associateBy { it.modernTitleId }
        }

        val networkFetched = sepTitles.second
            .chunked(100) // 100 is the limit for one fetch operation
            .flatMap { ids ->
                runWithTries {
                    service.storeBatch(
                        body = StoreBatchRequest(
                            pfns = null,
                            titleIds = ids.map(Long::toString)
                        )
                    ).titles
                }
            }.onEach { title ->
                database.put("titles.${title.titleId}", titleAdapter.toJson(title))
            }

        return@withContext (localFetched + networkFetched).associateBy { it.modernTitleId }
    }

    private suspend fun <T> runWithTries(tries: Int = 3, delay: Long = 250, action: suspend () -> T): T {
        var latestCatchedException: Exception = Exception()

        for (currentTry in 0..tries) {
            try {
                return action()
            } catch (e: Exception) {
                latestCatchedException = e
                delay(delay)
            }
        }

        throw latestCatchedException
    }
}