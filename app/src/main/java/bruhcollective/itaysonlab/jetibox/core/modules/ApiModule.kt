package bruhcollective.itaysonlab.jetibox.core.modules

import bruhcollective.itaysonlab.jetibox.core.service.ContentBuilderService
import bruhcollective.itaysonlab.jetibox.core.service.PeopleHubService
import bruhcollective.itaysonlab.jetibox.core.service.TitleHubService
import bruhcollective.itaysonlab.jetibox.core.ext.create
import bruhcollective.itaysonlab.jetibox.core.service.DisplayCatalogService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    @Provides
    @Singleton
    fun provideContentBuilder(
        @Named("xalRetrofit") retrofit: Retrofit
    ): ContentBuilderService = retrofit.create("https://contentbuilder.xboxlive.com")

    @Provides
    @Singleton
    fun provideTitleHub(
        @Named("xalRetrofit") retrofit: Retrofit
    ): TitleHubService = retrofit.create("https://titlehub.xboxlive.com")

    @Provides
    @Singleton
    fun providePeopleHub(
        @Named("xalRetrofit") retrofit: Retrofit
    ): PeopleHubService = retrofit.create("https://peoplehub.xboxlive.com")

    @Provides
    @Singleton
    fun provideDisplayCatalog(
        @Named("xalRetrofit") retrofit: Retrofit
    ): DisplayCatalogService = retrofit.create("https://displaycatalog.md.mp.microsoft.com")
}