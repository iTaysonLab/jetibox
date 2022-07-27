package bruhcollective.itaysonlab.jetibox.core.modules

import bruhcollective.itaysonlab.jetibox.core.config.ConfigService
import bruhcollective.itaysonlab.jetibox.core.ext.interceptRequest
import bruhcollective.itaysonlab.jetibox.core.xal_bridge.XalBridge
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder().build()

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
        @Named("xalOkhttp") okHttpClient: OkHttpClient
    ) = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create())
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