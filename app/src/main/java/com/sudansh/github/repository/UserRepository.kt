package com.sudansh.github.repository

import android.arch.lifecycle.LiveData
import com.sudansh.github.AppExecutors
import com.sudansh.github.api.ApiService
import com.sudansh.github.db.UserDao
import com.sudansh.github.testing.OpenForTesting
import com.sudansh.github.vo.Resource
import com.sudansh.github.vo.User
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository that handles User objects.
 */
@OpenForTesting
@Singleton
class UserRepository @Inject constructor(
	private val appExecutors: AppExecutors,
	private val userDao: UserDao,
	private val apiService: ApiService
) {

	fun loadUser(login: String): LiveData<Resource<User>> {
		return object : NetworkBoundResource<User, User>(appExecutors) {
			override fun saveCallResult(item: User) {
				userDao.insert(item)
			}

			override fun shouldFetch(data: User?) = data == null

			override fun loadFromDb() = userDao.findByLogin(login)

			override fun createCall() = apiService.getUser(login)
		}.asLiveData()
	}
}
