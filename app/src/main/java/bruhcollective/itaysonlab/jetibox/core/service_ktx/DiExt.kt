package bruhcollective.itaysonlab.jetibox.core.service_ktx

import okhttp3.OkHttpClient
import okhttp3.Request

fun OkHttpClient.Builder.interceptRequest(scope: Request.Builder.(Request) -> Unit) = addInterceptor { chain ->
  val request = chain.request()
  chain.proceed(request.newBuilder().apply { scope(this, request) }.build())
}