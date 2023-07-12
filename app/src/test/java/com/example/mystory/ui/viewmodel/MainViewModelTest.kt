package com.example.mystory.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import com.example.mystory.*
import com.example.mystory.data.local.MyStoryModel
import com.example.mystory.data.repository.MyStoryRepository
import com.example.mystory.data.response.LoginResult
import com.example.mystory.ui.adapter.MyStoryAdapter
import com.example.mystory.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {

    @get:Rule
    val instatExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var repository: MyStoryRepository
    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        viewModel = MainViewModel(repository)
    }

    @Test
    fun `When User Account Should Not Null`() {
        val dummyUser = DataDummy.generateDummyUserLoginResponse()
        val expectedUser = MutableLiveData<LoginResult>()
        expectedUser.value = dummyUser
        Mockito.`when`(repository.getUserToken()).thenReturn(expectedUser.asFlow())

        val actualUser = viewModel.getUserToken().getOrAwaitValue()
        Mockito.verify(repository).getUserToken()
        assertNotNull(actualUser)
    }

    @Test
    fun `When Get Story Should Not Null and Return Success`() = runTest {
        val dummyStory = DataDummy.generateDummyStoryResponse().listStory
        val data: PagingData<MyStoryModel> = QuotePagingSource.snapshot(dummyStory!!)
        val expectedStory = MutableLiveData<PagingData<MyStoryModel>>()
        expectedStory.value = data
        Mockito.`when`(repository.getStories(TOKEN)).thenReturn(expectedStory)

        val actualStory: PagingData<MyStoryModel> = viewModel.getStories(TOKEN).getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = MyStoryAdapter.DIFF_CALLBACK,
            updateCallback = NoopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStory)
        assertNotNull(differ.snapshot())
        assertEquals(dummyStory, differ.snapshot())
        assertEquals(dummyStory.size, differ.snapshot().size)
        assertEquals(dummyStory[0].name, differ.snapshot()[0]!!.name)
    }

    companion object {
        private const val TOKEN = "token"
    }
}