package bruhcollective.itaysonlab.jetibox.core.ext

import retrofit2.Retrofit
import retrofit2.create

inline fun <reified T> Retrofit.create(baseUrl: String): T
    = newBuilder().baseUrl(baseUrl).build().create(T::class.java)