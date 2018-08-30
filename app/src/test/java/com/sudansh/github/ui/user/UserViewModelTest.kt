package com.sudansh.github.ui.user

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import com.sudansh.github.repository.RepoRepository
import com.sudansh.github.repository.UserRepository
import com.sudansh.github.util.TestUtil
import com.sudansh.github.util.mock
import com.sudansh.github.vo.Repo
import com.sudansh.github.vo.Resource
import com.sudansh.github.vo.User
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.`when`
import org.mockito.Mockito.any
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.reset
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions

@RunWith(JUnit4::class)
class UserViewModelTest {
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private val userRepository = mock(UserRepository::class.java)
    private val repoRepository = mock(RepoRepository::class.java)
    private val userViewModel = UserViewModel(userRepository, repoRepository)

    @Test
    fun testNull() {
        assertThat(userViewModel.user, notNullValue())
        verify(userRepository, never()).loadUser(anyString())
        userViewModel.setLogin("foo")
        verify(userRepository, never()).loadUser(anyString())
    }

    @Test
    fun testCallRepo() {
        userViewModel.user.observeForever(mock())
        userViewModel.setLogin("abc")
        verify(userRepository).loadUser("abc")
        reset(userRepository)
        userViewModel.setLogin("ddd")
        verify(userRepository).loadUser("ddd")
    }

    @Test
    fun sendResultToUI() {
        val foo = MutableLiveData<Resource<User>>()
        val bar = MutableLiveData<Resource<User>>()
        `when`(userRepository.loadUser("foo")).thenReturn(foo)
        `when`(userRepository.loadUser("bar")).thenReturn(bar)
        val observer = mock<Observer<Resource<User>>>()
        userViewModel.user.observeForever(observer)
        userViewModel.setLogin("foo")
        verify(observer, never()).onChanged(any())
        val fooUser = TestUtil.createUser("foo")
        val fooValue = Resource.success(fooUser)

        foo.value = fooValue
        verify(observer).onChanged(fooValue)
        reset(observer)
        val barUser = TestUtil.createUser("bar")
        val barValue = Resource.success(barUser)
        bar.value = barValue
        userViewModel.setLogin("bar")
        verify(observer).onChanged(barValue)
    }

    @Test
    fun loadRepositories() {
        userViewModel.repositories.observeForever(mock())
        verifyNoMoreInteractions(repoRepository)
        userViewModel.setLogin("foo")
        verify(repoRepository).loadRepos("foo")
        reset(repoRepository)
        userViewModel.setLogin("bar")
        verify(repoRepository).loadRepos("bar")
        verifyNoMoreInteractions(userRepository)
    }

    @Test
    fun retry() {
        userViewModel.setLogin("foo")
        verifyNoMoreInteractions(repoRepository, userRepository)
        userViewModel.retry()
        verifyNoMoreInteractions(repoRepository, userRepository)
        val userObserver = mock<Observer<Resource<User>>>()
        userViewModel.user.observeForever(userObserver)
        val repoObserver = mock<Observer<Resource<List<Repo>>>>()
        userViewModel.repositories.observeForever(repoObserver)

        verify(userRepository).loadUser("foo")
        verify(repoRepository).loadRepos("foo")
        reset(userRepository, repoRepository)

        userViewModel.retry()
        verify(userRepository).loadUser("foo")
        verify(repoRepository).loadRepos("foo")
        reset(userRepository, repoRepository)
        userViewModel.user.removeObserver(userObserver)
        userViewModel.repositories.removeObserver(repoObserver)

        userViewModel.retry()
        verifyNoMoreInteractions(userRepository, repoRepository)
    }

    @Test
    fun nullUser() {
        val observer = mock<Observer<Resource<User>>>()
        userViewModel.setLogin("foo")
        userViewModel.setLogin(null)
        userViewModel.user.observeForever(observer)
        verify(observer).onChanged(null)
    }

    @Test
    fun nullRepoList() {
        val observer = mock<Observer<Resource<List<Repo>>>>()
        userViewModel.setLogin("foo")
        userViewModel.setLogin(null)
        userViewModel.repositories.observeForever(observer)
        verify(observer).onChanged(null)
    }

    @Test
    fun dontRefreshOnSameData() {
        val observer = mock<Observer<String>>()
        userViewModel.login.observeForever(observer)
        verifyNoMoreInteractions(observer)
        userViewModel.setLogin("foo")
        verify(observer).onChanged("foo")
        reset(observer)
        userViewModel.setLogin("foo")
        verifyNoMoreInteractions(observer)
        userViewModel.setLogin("bar")
        verify(observer).onChanged("bar")
    }

    @Test
    fun noRetryWithoutUser() {
        userViewModel.retry()
        verifyNoMoreInteractions(userRepository, repoRepository)
    }
}