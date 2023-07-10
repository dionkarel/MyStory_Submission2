package com.example.mystory.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.paging.*
import com.example.mystory.db.MyStoryDao
import com.example.mystory.db.MyStoryDb
import com.example.mystory.data.local.MyStoryModel
import com.example.mystory.data.local.UserPreference
import com.example.mystory.network.ApiService
import com.example.mystory.data.remote.MyStoryRemoteMediator
import com.example.mystory.data.response.AddStoryResponse
import com.example.mystory.data.response.LoginResponse
import com.example.mystory.data.response.LoginResult
import com.example.mystory.data.response.RegisterResponse
import com.example.mystory.data.response.*
import com.example.mystory.util.Result
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MyStoryRepository private constructor(
    private val apiService: ApiService,
    private val myStoryDao: MyStoryDao,
    private val myStoryDb: MyStoryDb,
    private val userPreference: UserPreference
) {

    private val loginResult = MediatorLiveData<Result<LoginResult>>()
    private val registerResult = MediatorLiveData<Result<RegisterResponse>>()
    private val uploadResult = MediatorLiveData<Result<AddStoryResponse>>()
    private val storyLocation = MediatorLiveData<Result<List<MyStoryModel>>>()

    fun getStories(token: String): LiveData<PagingData<MyStoryModel>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = MyStoryRemoteMediator(apiService, myStoryDb, token),
            pagingSourceFactory = {
                myStoryDao.getStories()
            }
        ).liveData
    }

    fun postLogin(email: String, password: String): LiveData<Result<LoginResult>> {
        loginResult.value = Result.Loading
        val client = apiService.postLogin(email, password)
        client.enqueue(object : Callback<LoginResponse> {

            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()!!
                    if (!responseBody.error) {
                        val user = LoginResult(
                            responseBody.loginResult.userId,
                            responseBody.loginResult.name,
                            responseBody.loginResult.token
                        )
                        loginResult.value = Result.Success(user)
                    } else {
                        Log.e("LOGIN_ERROR", "onError: ${responseBody.message}")
                        loginResult.value = Result.Error(responseBody.message)
                    }
                } else {
                    Log.e("LOGIN_ERROR", "onError: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                loginResult.value = Result.Error(t.message.toString())
            }

        })

        return loginResult
    }

    fun postRegister(name: String, email: String, password: String): LiveData<Result<RegisterResponse>> {
        registerResult.value = Result.Loading
        val client = apiService.postRegister(name, email, password)
        client.enqueue(object : Callback<RegisterResponse> {

            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()!!
                    if (!responseBody.error) {
                        registerResult.value = Result.Success(responseBody)
                    } else {
                        registerResult.value = Result.Error(responseBody.message)
                    }
                } else {
                    registerResult.value = Result.Error(response.message().toString())
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                registerResult.value = Result.Error(t.message.toString())
            }

        })

        return registerResult
    }

    fun getDetailStories(id: String): LiveData<Result<MyStoryModel>> = liveData {
        emit(Result.Loading)
        try {
            val localData: LiveData<Result<MyStoryModel>> = myStoryDao.getDetailStory(id).map {
                Result.Success(it)
            }
            emitSource(localData)
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun uploadImageStory(
        token: String,
        imageMultipartBody: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?
    ): LiveData<Result<AddStoryResponse>> {
        uploadResult.value = Result.Loading
        val client = apiService.postStory("Bearer $token", imageMultipartBody, description, lat, lon)
        client.enqueue(object : Callback<AddStoryResponse> {

            override fun onResponse(
                call: Call<AddStoryResponse>,
                response: Response<AddStoryResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()!!
                    if (!responseBody.error) {
                        uploadResult.value = Result.Success(responseBody)
                    } else {
                        uploadResult.value = Result.Error(responseBody.message)
                    }
                } else {
                    uploadResult.value = Result.Error(response.message())
                }
            }

            override fun onFailure(call: Call<AddStoryResponse>, t: Throwable) {
                uploadResult.value = Result.Error(t.message.toString())
            }

        })

        return uploadResult
    }

    fun getStoryLocation(token: String): LiveData<Result<List<MyStoryModel>>> {
        storyLocation.value = Result.Loading
        val client =  apiService.getStoryLocation(
            "Bearer $token",
            100,
            true
        )
        client.enqueue(object :  Callback<GetAllStoryResponse> {

            override fun onResponse(
                call: Call<GetAllStoryResponse>,
                response: Response<GetAllStoryResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()!!
                    if (!responseBody.error) {
                        storyLocation.value = Result.Success(responseBody.listStory!!)
                    } else {
                        storyLocation.value = Result.Error(responseBody.message)
                    }
                } else {
                    storyLocation.value = Result.Error(response.message())
                }
            }
            override fun onFailure(call: Call<GetAllStoryResponse>, t: Throwable) {
                storyLocation.value = Result.Error(t.message.toString())
            }
        })

        return storyLocation
    }

    fun getUserToken() = userPreference.getUserToken()

    suspend fun userSave(user: LoginResult) {
        userPreference.userSave(user)
    }

    suspend fun userLogout() {
        userPreference.userLogout()
    }

    companion object {

        @Volatile
        private var INSTANCE: MyStoryRepository? = null

        fun getInstance(
            apiService: ApiService,
            myStoryDao: MyStoryDao,
            myStoryDb: MyStoryDb,
            userPreference: UserPreference
        ): MyStoryRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: MyStoryRepository(
                    apiService,
                    myStoryDao,
                    myStoryDb,
                    userPreference
                )
            }.also {
                INSTANCE = it
            }
    }
}