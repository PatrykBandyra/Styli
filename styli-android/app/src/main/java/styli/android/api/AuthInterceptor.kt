package styli.android.api

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response
import styli.android.global.AppPreferences

class AuthInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val responseBuilder = chain.request().newBuilder()
        return chain.proceed(
            AppPreferences.jwt?.let { jwt: String ->
                return@let responseBuilder.addHeader("Authorization", "Bearer $jwt").build()
            } ?: responseBuilder.build()
        )
    }
}