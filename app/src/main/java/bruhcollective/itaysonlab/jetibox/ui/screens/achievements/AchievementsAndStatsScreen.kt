package bruhcollective.itaysonlab.jetibox.ui.screens.achievements

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import bruhcollective.itaysonlab.jetibox.core.config.MsCapDatabase
import bruhcollective.itaysonlab.jetibox.core.models.titlehub.StoreBatchRequest
import bruhcollective.itaysonlab.jetibox.core.models.titlehub.Title
import bruhcollective.itaysonlab.jetibox.core.service.TitleHubService
import bruhcollective.itaysonlab.jetibox.ui.screens.store.TitleStoreScreenViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@Composable
fun AchievementsAndStatsScreen (
    titleId: String,
    viewModel: AchievementsAndStatsScreenViewModel = hiltViewModel()
) {

}

@HiltViewModel
class AchievementsAndStatsScreenViewModel @Inject constructor(
    private val thApi: TitleHubService,
    private val msCapDatabase: MsCapDatabase
) : ViewModel() {
    var content by mutableStateOf<StoreData>(StoreData.Loading)
        private set

    suspend fun load(titleId: String) {

    }

    sealed class StoreData {
        class Loaded(
            val xuid: String,
            val app: Title,
            val appPersonal: Title,
            val matchedCaps: List<String>
        ) : StoreData()

        object Loading : StoreData()
    }
}