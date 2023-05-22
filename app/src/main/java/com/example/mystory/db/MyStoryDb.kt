package com.example.mystory.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.mystory.data.local.MyStoryModel
import com.example.mystory.data.local.RemoteKeys

@Database(
    entities = [MyStoryModel::class, RemoteKeys::class],
    version = 1,
    exportSchema = false
)
abstract class MyStoryDb : RoomDatabase() {
    abstract fun storyDao(): MyStoryDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {

        @Volatile
        private var INSTANCE: MyStoryDb? = null

        fun getInstance(context: Context): MyStoryDb {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    MyStoryDb::class.java,
                    "my_story.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also {
                        INSTANCE = it
                    }
            }
        }
    }
}