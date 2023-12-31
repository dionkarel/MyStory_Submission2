package com.example.mystory.network

import com.example.mystory.data.response.AddStoryResponse
import com.example.mystory.data.response.GetAllStoryResponse
import com.example.mystory.data.response.LoginResponse
import com.example.mystory.data.response.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    fun postRegister(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    fun postLogin(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @Multipart
    @POST("stories")
    fun postStory(
        @Header("Authorization") Bearer: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody? = null,
        @Part("lon") lon: RequestBody? = null
    ): Call<AddStoryResponse>

    @GET("stories")
    suspend fun getStoryList(
        @Header("Authorization") Bearer: String,
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null
    ): GetAllStoryResponse

    @GET("stories?location=1")
    fun getStoryLocation(
        @Header("Authorization") Bearer: String,
        @Query("size") size: Int? = null,
        @Query("location") location: Boolean? = null
    ): Call<GetAllStoryResponse>
}