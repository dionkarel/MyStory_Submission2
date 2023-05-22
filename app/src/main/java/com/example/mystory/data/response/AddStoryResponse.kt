package com.example.mystory.data.response

import com.example.mystory.data.local.MyStoryModel
import com.google.gson.annotations.SerializedName

data class AddStoryResponse (
    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("listStory")
    val listStory: List<MyStoryModel>?

)