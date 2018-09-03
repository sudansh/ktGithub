package com.sudansh.github.ui.search

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import com.sudansh.github.repository.RepoRepository
import com.sudansh.github.vo.Resource
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*

class NextPageHandlerTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val repository = mock(RepoRepository::class.java)

    private lateinit var pageHandler: SearchViewModel.NextPageHandler

    @Before
    fun init() {
        pageHandler = SearchViewModel.NextPageHandler(repository)
    }

    private val status: SearchViewModel.LoadMoreState?
        get() = pageHandler.loadMoreState.value

    @Test
    fun constructor() {
        val initial = status
        assertThat<SearchViewModel.LoadMoreState>(initial, notNullValue())
        assertThat(initial?.isRunning, `is`(false))
        assertThat(initial?.errorMessage, nullValue())
    }

    @Test
    fun reloadSameValue() {
        enqueueResponse("foo")
        pageHandler.queryNextPage("foo")
        verify(repository).searchNextPage("foo")

        reset(repository)
        pageHandler.queryNextPage("foo")
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun success() {
        val liveData = enqueueResponse("foo")

        pageHandler.queryNextPage("foo")
        verify(repository).searchNextPage("foo")
        assertThat(liveData.hasActiveObservers(), `is`(true))
        pageHandler.onChanged(Resource.loading(null))
        assertThat(liveData.hasActiveObservers(), `is`(true))
        assertThat(status?.isRunning, `is`(true))

        pageHandler.onChanged(Resource.success(true))
        assertThat(liveData.hasActiveObservers(), `is`(false))
        assertThat(pageHandler.hasMore, `is`(true))
        assertThat(status?.isRunning, `is`(false))
        assertThat(liveData.hasActiveObservers(), `is`(false))

        // requery
        reset(repository)
        val nextPage = enqueueResponse("foo")
        pageHandler.queryNextPage("foo")
        verify(repository).searchNextPage("foo")
        assertThat(nextPage.hasActiveObservers(), `is`(true))

        pageHandler.onChanged(Resource.success(false))
        assertThat(liveData.hasActiveObservers(), `is`(false))
        assertThat(pageHandler.hasMore, `is`(false))
        assertThat(status?.isRunning, `is`(false))
        assertThat(nextPage.hasActiveObservers(), `is`(false))

        // retry, no query
        reset(repository)
        pageHandler.queryNextPage("foo")
        verifyNoMoreInteractions(repository)
        pageHandler.queryNextPage("foo")
        verifyNoMoreInteractions(repository)

        // query another
        val bar = enqueueResponse("bar")
        pageHandler.queryNextPage("bar")
        verify(repository).searchNextPage("bar")
        assertThat(bar.hasActiveObservers(), `is`(true))
    }

    @Test
    fun failure() {
        val liveData = enqueueResponse("foo")
        pageHandler.queryNextPage("foo")
        assertThat(liveData.hasActiveObservers(), `is`(true))
        pageHandler.onChanged(Resource.error("idk", false))
        assertThat(liveData.hasActiveObservers(), `is`(false))
        assertThat(status?.errorMessage, `is`("idk"))
        assertThat(status?.errorMessageIfNotHandled, `is`("idk"))
        assertThat(status?.errorMessageIfNotHandled, nullValue())
        assertThat(status?.isRunning, `is`(false))
        assertThat(pageHandler.hasMore, `is`(true))

        reset(repository)
        val liveData2 = enqueueResponse("foo")
        pageHandler.queryNextPage("foo")
        assertThat(liveData2.hasActiveObservers(), `is`(true))
        assertThat(status?.isRunning, `is`(true))
        pageHandler.onChanged(Resource.success(false))
        assertThat(status?.isRunning, `is`(false))
        assertThat(status?.errorMessage, `is`(nullValue()))
        assertThat(pageHandler.hasMore, `is`(false))
    }

    @Test
    fun nullOnChanged() {
        val liveData = enqueueResponse("foo")
        pageHandler.queryNextPage("foo")
        assertThat(liveData.hasActiveObservers(), `is`(true))
        pageHandler.onChanged(null)
        assertThat(liveData.hasActiveObservers(), `is`(false))
    }

    private fun enqueueResponse(query: String): MutableLiveData<Resource<Boolean>> {
        val liveData = MutableLiveData<Resource<Boolean>>()
        `when`(repository.searchNextPage(query)).thenReturn(liveData)
        return liveData
    }
}