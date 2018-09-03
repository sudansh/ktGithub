package com.sudansh.github.ui.repo

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import com.sudansh.github.repository.RepoRepository
import com.sudansh.github.util.mock
import com.sudansh.github.vo.Contributor
import com.sudansh.github.vo.Repo
import com.sudansh.github.vo.Resource
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.*

@RunWith(JUnit4::class)
class RepoViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val repository = mock(RepoRepository::class.java)
    private var repoViewModel = RepoViewModel(repository)

    @Test
    fun testNull() {
        assertThat(repoViewModel.repo, notNullValue())
        assertThat(repoViewModel.contributors, notNullValue())
        verify(repository, never()).loadRepo(anyString(), anyString())
    }

    @Test
    fun dontFetchWithoutObservers() {
        repoViewModel.setId("a", "b")
        verify(repository, never()).loadRepo(anyString(), anyString())
    }

    @Test
    fun fetchWhenObserved() {
        repoViewModel.setId("a", "b")
        repoViewModel.repo.observeForever(mock())
        verify(repository).loadRepo("a", "b")
    }

    @Test
    fun changeWhileObserved() {
        repoViewModel.repo.observeForever(mock())

        repoViewModel.setId("a", "b")
        repoViewModel.setId("c", "d")

        verify(repository).loadRepo("a", "b")
        verify(repository).loadRepo("c", "d")
    }

    @Test
    fun contributors() {
        val observer = mock<Observer<Resource<List<Contributor>>>>()
        repoViewModel.contributors.observeForever(observer)
        verifyNoMoreInteractions(observer)
        verifyNoMoreInteractions(repository)
        repoViewModel.setId("foo", "bar")
        verify(repository).loadContributors("foo", "bar")
    }

    @Test
    fun resetId() {
        val observer = mock<Observer<RepoViewModel.RepoId>>()
        repoViewModel.repoId.observeForever(observer)
        verifyNoMoreInteractions(observer)
        repoViewModel.setId("foo", "bar")
        verify(observer).onChanged(RepoViewModel.RepoId("foo", "bar"))
        reset(observer)
        repoViewModel.setId("foo", "bar")
        verifyNoMoreInteractions(observer)
        repoViewModel.setId("a", "b")
        verify(observer).onChanged(RepoViewModel.RepoId("a", "b"))
    }

    @Test
    fun retry() {
        repoViewModel.retry()
        verifyNoMoreInteractions(repository)
        repoViewModel.setId("foo", "bar")
        verifyNoMoreInteractions(repository)
        val observer = mock<Observer<Resource<Repo>>>()
        repoViewModel.repo.observeForever(observer)
        verify(repository).loadRepo("foo", "bar")
        reset(repository)
        repoViewModel.retry()
        verify(repository).loadRepo("foo", "bar")
    }

    @Test
    fun nullRepoId() {
        repoViewModel.setId(null, null)
        val observer1 = mock<Observer<Resource<Repo>>>()
        val observer2 = mock<Observer<Resource<List<Contributor>>>>()
        repoViewModel.repo.observeForever(observer1)
        repoViewModel.contributors.observeForever(observer2)
        verify(observer1).onChanged(null)
        verify(observer2).onChanged(null)
    }
}