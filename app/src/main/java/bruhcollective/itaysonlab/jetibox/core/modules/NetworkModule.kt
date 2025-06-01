package bruhcollective.itaysonlab.jetibox.core.modules

import bruhcollective.itaysonlab.jetibox.core.config.ConfigService
import bruhcollective.itaysonlab.jetibox.core.ext.interceptRequest
import bruhcollective.itaysonlab.jetibox.core.models.contentbuilder.ContentBuilderLayer
import bruhcollective.itaysonlab.jetibox.core.models.mediahub.ContentLocator
import bruhcollective.itaysonlab.jetibox.core.xal_bridge.XalBridge
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.plus
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.Locale
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        encodeDefaults = true
        serializersModule += ContentBuilderLayer.serializerModule
        serializersModule += ContentLocator.serializerModule
    }

    @Provides
    @Singleton
    @Named("userAgent")
    fun provideUserAgent() = "XAL Android 2021.11.20211021.000"

    @Provides
    @Singleton
    @Named("xalOkhttp")
    fun provideOkhttp(
        @Named("userAgent") userAgent: String,
        xalBridge: XalBridge
    ) = OkHttpClient.Builder().interceptRequest { request ->
        header("Accept-Language", Locale.getDefault().language)
        header("User-Agent", userAgent)
        header("MS-CV", xalBridge.correlationVector)

        (xalBridge.getTokenAndSignature(request.url.toString(), false))?.let {
            header("Authorization", it.token)
            header("Signature", it.signature)
        }
    }.build()

    @Provides
    @Singleton
    @Named("xalRetrofit")
    fun provideRetrofit(
        @Named("xalOkhttp") okHttpClient: OkHttpClient,
        json: Json
    ) = Retrofit.Builder()
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .client(okHttpClient)
        .baseUrl("https://microsoft.com") // this should be overriden later
        .build()

    @Provides
    @Singleton
    fun provideXalBridge() = XalBridge()

    @Provides
    @Singleton
    fun provideConfigService() = ConfigService()
}