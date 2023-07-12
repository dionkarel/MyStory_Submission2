package com.example.mystory.util

import com.example.mystory.data.local.MyStoryModel
import com.example.mystory.data.response.AddStoryResponse
import com.example.mystory.data.response.GetAllStoryResponse
import com.example.mystory.data.response.LoginResult
import com.example.mystory.data.response.RegisterResponse

object DataDummy {

    fun generateDummyGetAllStoryResponse(): MyStoryModel {
        return MyStoryModel(
            "id",
            "name",
            "description",
            "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png",
            "2022-02-22T22:22:22Z",
            null,
            null,
        )
    }

    fun generateDummyStoryResponse(): GetAllStoryResponse {
        val items: MutableList<MyStoryModel> = arrayListOf()
        for (i in 1..100) {
            val story = MyStoryModel(
                "id $i",
                "name",
                "description",
                "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png",
                "2022-02-22T22:22:22Z",
                null,
                null,
            )
            items.add(story)
        }
        return GetAllStoryResponse(
            false,
            "Data story berhasil didapatkan",
            items
        )
    }

    fun generateDummyStoryMapsResponse(): GetAllStoryResponse {
        val items: MutableList<MyStoryModel> = arrayListOf()
        for (i in 0..100) {
            val story = MyStoryModel(
                "id $i",
                "name",
                "description",
                "https://dicoding-web-img.sgp1.cdn.digitaloceanspaces.com/original/commons/feature-1-kurikulum-global-3.png",
                "2022-02-22T22:22:22Z",
                -7.331406,
                112.782157,
            )
            items.add(story)
        }
        return GetAllStoryResponse(
            false,
            "Data story berhasil didapatkan",
            items
        )
    }

    fun generateDummyUserLoginResponse(): LoginResult {
        return LoginResult(
            "id",
            "name",
            "token"
        )
    }

    fun generateDummyUserRegisterResponse(): RegisterResponse {
        return RegisterResponse(
            false,
            "User Created"
        )
    }

    fun generateDummyAddNewStoryResponse(): AddStoryResponse {
        return AddStoryResponse(
            false,
            "Success",
            null
        )
    }

}