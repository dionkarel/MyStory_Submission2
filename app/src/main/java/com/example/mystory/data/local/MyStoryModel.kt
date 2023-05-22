package com.example.mystory.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "my_story")
data class MyStoryModel (
    @PrimaryKey
    @field:ColumnInfo(name = "id")
    @field:SerializedName("id")
    val id: String,

    @field:ColumnInfo(name = "name")
    @field:SerializedName("name")
    val name: String,

    @field:ColumnInfo(name = "description")
    @field:SerializedName("description")
    val description: String,

    @field:ColumnInfo(name = "photoUrl")
    @field:SerializedName("photoUrl")
    val photoUrl: String,

    @field:ColumnInfo(name = "createdAt")
    @field:SerializedName("createdAt")
    val createdAt: String,

    @field:ColumnInfo(name = "lat")
    @field:SerializedName("lat")
    val lat: Double?,

    @field:ColumnInfo(name = "lon")
    @field:SerializedName("lon")
    val lon: Double?

    )