package com.example.mystory.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.mystory.db.MyStoryDb
import com.example.mystory.data.local.UserPreference
import com.example.mystory.network.ApiConfig
import com.example.mystory.data.repository.MyStoryRepository

object Injection {

    fun provideRepository(
        context: Context,
        dataStore: DataStore<Preferences>
    ): MyStoryRepository {
        val apiService = ApiConfig.getApi()
        val storyDb = MyStoryDb.getInstance(context)
        val storyDao = storyDb.storyDao()
        val userPreference = UserPreference.getInstance(dataStore)

        return MyStoryRepository.getInstance(apiService, storyDao, storyDb, userPreference)

    }
}