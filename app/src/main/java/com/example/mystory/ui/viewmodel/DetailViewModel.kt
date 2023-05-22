package com.example.mystory.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mystory.data.repository.MyStoryRepository

class DetailViewModel(private val repository: MyStoryRepository) : ViewModel() {

    fun getDetailStories(id: String) = repository.getDetailStories(id)

}