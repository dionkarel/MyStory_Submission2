package com.example.mystory.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.mystory.data.local.MyStoryModel
import com.example.mystory.data.repository.MyStoryRepository
import kotlinx.coroutines.launch

class MainViewModel(private val repository: MyStoryRepository) : ViewModel() {

    fun getUserToken() = repository.getUserToken().asLiveData()

    fun userLogout() {
        viewModelScope.launch {
            repository.userLogout()
        }
    }

    fun getStories(token: String): LiveData<PagingData<MyStoryModel>> =
        repository.getStories(token).cachedIn(viewModelScope)

}