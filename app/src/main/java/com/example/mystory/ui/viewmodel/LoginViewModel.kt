package com.example.mystory.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mystory.data.repository.MyStoryRepository
import com.example.mystory.data.response.LoginResult
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: MyStoryRepository) : ViewModel() {

    fun postLogin(email: String, password: String) =
        repository.postLogin(email, password)

    fun userSave(user: LoginResult) {
        viewModelScope.launch {
            repository.userSave(user)
        }
    }

}