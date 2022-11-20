package styli.android.api

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*
import styli.android.api.dto.auth.LoginForm
import styli.android.api.dto.auth.RegisterForm
import styli.android.api.dto.effect.EffectRequest
import styli.android.api.dto.image.ImageDetails

interface Api {

    // Authentication
    @POST("auth/login")
    suspend fun login(@Body loginForm: LoginForm): Response<>

    @POST("auth/register")
    suspend fun register(@Body registerForm: RegisterForm): Response<>

    // Effects
    @GET("effect")
    suspend fun getAvailableEffects(): Response<List<String>>

    @POST("effect")
    @Multipart
    suspend fun applyEffect(
        @Part("effectRequest") effectRequest: EffectRequest,
        @Part("image") image: MultipartBody.Part,
        @Part("image2") image2: MultipartBody.Part?
    ) : Response<>

    // Images
    @GET("image")
    suspend fun getImagesPaginated(@Query("page") page: Int, @Query("size") size: Int) : Response<>

    @POST("image")
    suspend fun uploadImage(
        @Part("imageDetails") imageDetails: ImageDetails,
        @Part("image") image: MultipartBody.Part
    ) : Response<>

    @DELETE("image")
    suspend fun deleteImage(@Query("id") imageId: Int) : Response<>
}