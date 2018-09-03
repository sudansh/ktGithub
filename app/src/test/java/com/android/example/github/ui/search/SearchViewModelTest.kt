package com.android.example.github.ui.search


import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import com.android.example.github.repository.RepoRepository
import com.android.example.github.util.mock
import com.android.example.github.vo.Repo
import com.android.example.github.vo.Resource
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.*

@RunWith(JUnit4::class)
class SearchViewModelTest {
    @get:Rule
    val instantExecutor = InstantTaskExecutorRule()
    private val repository = mock(RepoRepository::class.java)
    private lateinit var viewModel: SearchViewModel

    @Before
    fun init() {
        // need ot init after instant executor rule is established.
        viewModel = SearchViewModel(repository)
    }

    @Test
    fun empty() {
        val result = mock<Observer<Resource<List<Repo>>>>()
        viewModel.results.observeForever(result)
        viewModel.loadNextPage()
        verifyNoMoreInteractions(repository)
    }

    @Test
    fun basic() {
        val result = mock<Observer<Resource<List<Repo>>>>()
        viewModel.results.observeForever(result)
        viewModel.setQuery("foo")
        verify(repository).search("foo")
        verify(repository, never()).searchNextPage("foo")
    }

    @Test
    fun noObserverNoQuery() {
        `when`(repository.searchNextPage("foo")).thenReturn(mock())
        viewModel.setQuery("foo")
        verify(repository, never()).search("foo")
        // next page is user interaction and even if loading state is not observed, we query
        // would be better to avoid that if main search query is not observed
        viewModel.loadNextPage()
        verify(repository).searchNextPage("foo")
    }

    @Test
    fun swap() {
        val nextPage = MutableLiveData<Resource<Boolean>>()
        `when`(repository.searchNextPage("foo")).thenReturn(nextPage)

        val result = mock<Observer<Resource<List<Repo>>>>()
        viewModel.results.observeForever(result)
        verifyNoMoreInteractions(repository)
        viewModel.setQuery("foo")
        verify(repository).search("foo")
        viewModel.loadNextPage()

        viewModel.loadMoreStatus.observeForever(mock())
        verify(repository).searchNextPage("foo")
        assertThat(nextPage.hasActiveObservers(), `is`(true))
        viewModel.setQuery("bar")
        assertThat(nextPage.hasActiveObservers(), `is`(false))
        verify(repository).search("bar")
        verify(repository, never()).searchNextPage("bar")
    }

    @Test
    fun refresh() {
        viewModel.refresh()
        verifyNoMoreInteractions(repository)
        viewModel.setQuery("foo")
        viewModel.refresh()
        verifyNoMoreInteractions(repository)
        viewModel.results.observeForever(mock())
        verify(repository).search("foo")
        reset(repository)
        viewModel.refresh()
        verify(repository).search("foo")
    }

    @Test
    fun resetSameQuery() {
        viewModel.results.observeForever(mock())
        viewModel.setQuery("foo")
        verify(repository).search("foo")
        reset(repository)
        viewModel.setQuery("FOO")
        verifyNoMoreInteractions(repository)
        viewModel.setQuery("bar")
        verify(repository).search("bar")
    }
}