package com.example.mystory.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import com.example.mystory.data.local.MyStoryModel
import com.example.mystory.data.repository.MyStoryRepository
import com.example.mystory.data.response.LoginResult
import com.example.mystory.util.DataDummy
import com.example.mystory.util.MainDispatcherRule
import com.example.mystory.util.Result
import com.example.mystory.util.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class MapsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var  repository: MyStoryRepository
    private lateinit var viewModel: MapsViewModel

    @Before
    fun setup() {
        viewModel = MapsViewModel(repository)
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
    fun `When Get Story with Location Should Not Null and Return Success`() {
        val dummyStory = DataDummy.generateDummyStoryMapsResponse()
        val expectedStory = MutableLiveData<Result<List<MyStoryModel>>>()
        expectedStory.value = Result.Success(dummyStory.listStory!!)
        Mockito.`when`(repository.getStoryLocation(TOKEN)).thenReturn(expectedStory)

        val actualStory = viewModel.getStoriesWithLocation(TOKEN).getOrAwaitValue()
        Mockito.verify(repository).getStoryLocation(TOKEN)
        assertNotNull(actualStory)
        assertTrue(actualStory is Result.Success)
        assertEquals(dummyStory.listStory!!.size, (actualStory as Result.Success).data.size)
    }

    companion object {
        private const val TOKEN = "token"
    }
}