package styli.android.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object HttpClient {

    val api: Api by lazy {
        Retrofit.Builder()
            .baseUrl("http://20.50.136.8:8000/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Api::class.java)
    }
}