package com.example.mystory.db

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mystory.data.local.MyStoryModel

@Dao
interface MyStoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addStories(stories: List<MyStoryModel>)

    @Query("SELECT * FROM my_story")
    fun getStories(): PagingSource<Int, MyStoryModel>

    @Query("SELECT * FROM my_story WHERE id = :id LIMIT 1")
    fun getDetailStory(id: String): LiveData<MyStoryModel>

    @Query("DELETE FROM my_story")
    suspend fun deleteAll()
}