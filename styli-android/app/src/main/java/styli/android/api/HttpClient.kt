package styli.android.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import styli.android.global.AppPreferences

object HttpClient {

    val api: Api? =
        Retrofit.Builder()
            .baseUrl("http://20.50.136.8:8000/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(AuthInterceptor())
                    .build()
            )
            .build()
            .create(Api::class.java)
    get() {
        if (AppPreferences.isUserLoggedIn() || AppPreferences.isUserLoggedOut()) {
            return field
        }
        return null
    }
}