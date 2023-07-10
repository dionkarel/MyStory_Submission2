package com.example.mystory.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.mystory.data.repository.MyStoryRepository

class MapsViewModel (private val repository: MyStoryRepository): ViewModel() {

    fun getUserToken() = repository.getUserToken().asLiveData()

    fun getStoriesWithLocation(token: String) = repository.getStoryLocation(token)

}