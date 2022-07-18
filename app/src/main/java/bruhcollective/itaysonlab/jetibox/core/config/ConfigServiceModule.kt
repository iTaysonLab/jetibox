package bruhcollective.itaysonlab.jetibox.core.config

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
}