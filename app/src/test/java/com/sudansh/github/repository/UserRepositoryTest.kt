package com.sudansh.github.repository

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import com.sudansh.github.api.ApiService
import com.sudansh.github.db.UserDao
import com.sudansh.github.util.ApiUtil
import com.sudansh.github.util.InstantAppExecutors
import com.sudansh.github.util.TestUtil
import com.sudansh.github.util.mock
import com.sudansh.github.vo.Resource
import com.sudansh.github.vo.User
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify

@RunWith(JUnit4::class)
class UserRepositoryTest {
    private val userDao = mock(UserDao::class.java)
    private val githubService = mock(ApiService::class.java)
    private val repo = UserRepository(InstantAppExecutors(), userDao, githubService)

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @Test
    fun loadUser() {
        repo.loadUser("abc")
        verify(userDao).findByLogin("abc")
    }

    @Test
    fun goToNetwork() {
        val dbData = MutableLiveData<User>()
        `when`(userDao!!.findByLogin("foo")).thenReturn(dbData)
        val user = TestUtil.createUser("foo")
        val call = ApiUtil.successCall(user)
        `when`(githubService!!.getUser("foo")).thenReturn(call)
        val observer = mock<Observer<Resource<User>>>()

        repo.loadUser("foo").observeForever(observer)
        verify(githubService, never()).getUser("foo")
        val updatedDbData = MutableLiveData<User>()
        `when`(userDao.findByLogin("foo")).thenReturn(updatedDbData)
        dbData.value = null
        verify(githubService).getUser("foo")
    }

    @Test
    fun dontGoToNetwork() {
        val dbData = MutableLiveData<User>()
        val user = TestUtil.createUser("foo")
        dbData.value = user
        `when`(userDao!!.findByLogin("foo")).thenReturn(dbData)
        val observer = mock<Observer<Resource<User>>>()
        repo.loadUser("foo").observeForever(observer)
        verify(githubService, never()).getUser("foo")
        verify(observer).onChanged(Resource.success(user))
    }
}