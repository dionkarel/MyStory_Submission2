package com.example.mystory.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.mystory.data.repository.MyStoryRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(private val storyRepository: MyStoryRepository) : ViewModel() {

    fun getUserToken() = storyRepository.getUserToken().asLiveData()

    fun uploadImage(
        token: String,
        imageMultipartBody: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?
    ) = storyRepository.uploadImageStory(token, imageMultipartBody, description, lat, lon)
}