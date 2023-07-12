package com.example.mystory.util

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.mystory.data.local.MyStoryModel

class QuotePagingSource: PagingSource<Int, LiveData<List<MyStoryModel>>>() {

    override fun getRefreshKey(state: PagingState<Int, LiveData<List<MyStoryModel>>>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<MyStoryModel>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }

    companion object {
        fun snapshot(items: List<MyStoryModel>): PagingData<MyStoryModel> {
            return PagingData.from(items)
        }
    }

}
