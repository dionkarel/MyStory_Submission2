package com.example.mystory.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mystory.data.repository.MyStoryRepository

class RegisterViewModel(private val repository: MyStoryRepository) : ViewModel() {

    fun postRegister(name: String, email: String, password: String) =
        repository.postRegister(name, email, password)
}