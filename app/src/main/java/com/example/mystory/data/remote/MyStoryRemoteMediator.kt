package com.example.mystory.data.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.mystory.db.MyStoryDb
import com.example.mystory.data.local.MyStoryModel
import com.example.mystory.data.local.RemoteKeys
import com.example.mystory.network.ApiService

@OptIn(ExperimentalPagingApi::class)
class MyStoryRemoteMediator (
    private val apiService: ApiService,
    private val myStoryDb: MyStoryDb,
    private val token: String
): RemoteMediator<Int, MyStoryModel>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MyStoryModel>
    ): MediatorResult {
        val page = when (loadType) {

            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }

            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }

            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {
            val responseData =
                apiService.getStoryList("Bearer $token", page, state.config.pageSize).listStory
            val endOfPaginationReached = responseData!!.isEmpty()

            myStoryDb.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    myStoryDb.storyDao().deleteAll()
                }

                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = responseData.map {
                    RemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                myStoryDb.remoteKeysDao().insertAll(keys)
                myStoryDb.storyDao().addStories(responseData)
            }

            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, MyStoryModel>): RemoteKeys? {
        return state.pages.lastOrNull {
            it.data.isNotEmpty()
        }?.data?.firstOrNull()?.let { position ->
            myStoryDb.remoteKeysDao().getRemoteKeysId(position.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, MyStoryModel>): RemoteKeys? {
        return state.pages.lastOrNull {
            it.data.isNotEmpty()
        }?.data?.lastOrNull()?.let { data ->
            myStoryDb.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, MyStoryModel>): RemoteKeys? {
        return state.anchorPosition?.let { data ->
            state.closestItemToPosition(data)?.id?.let { id ->
                myStoryDb.remoteKeysDao().getRemoteKeysId(id)
            }
        }
    }

    companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}