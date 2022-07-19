package bruhcollective.itaysonlab.jetibox.core.config

import bruhcollective.itaysonlab.jetibox.core.service.DisplayCatalogService
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ConfigServiceModule {
    @Provides
    @Singleton
    fun provideService() = ConfigService()

    @Provides
    @Singleton
    fun provideMsCapDatabase(
        cfgService: ConfigService,
        displayCatalogService: DisplayCatalogService,
        moshi: Moshi
    ) = MsCapDatabase(cfgService, displayCatalogService, moshi)
}