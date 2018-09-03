package com.sudansh.github.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import com.sudansh.github.AppExecutors
import com.sudansh.github.api.ApiService
import com.sudansh.github.api.ApiSuccessResponse
import com.sudansh.github.api.RepoSearchResponse
import com.sudansh.github.db.AppDatabase
import com.sudansh.github.db.RepoDao
import com.sudansh.github.testing.OpenForTesting
import com.sudansh.github.util.AbsentLiveData
import com.sudansh.github.util.RateLimiter
import com.sudansh.github.vo.Contributor
import com.sudansh.github.vo.Repo
import com.sudansh.github.vo.RepoSearchResult
import com.sudansh.github.vo.Resource
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository that handles Repo instances.
 *
 * unfortunate naming :/ .
 * Repo - value object name
 * Repository - type of this class.
 */
@Singleton
@OpenForTesting
class RepoRepository @Inject constructor(
	private val appExecutors: AppExecutors,
	private val db: AppDatabase,
	private val repoDao: RepoDao,
	private val apiService: ApiService
) {

	private val repoListRateLimit = RateLimiter<String>(10, TimeUnit.MINUTES)

	fun loadRepos(owner: String): LiveData<Resource<List<Repo>>> {
		return object : NetworkBoundResource<List<Repo>, List<Repo>>(appExecutors) {
			override fun saveCallResult(item: List<Repo>) {
				repoDao.insertRepos(item)
			}

			override fun shouldFetch(data: List<Repo>?): Boolean {
				return data == null || data.isEmpty() || repoListRateLimit.shouldFetch(owner)
			}

			override fun loadFromDb() = repoDao.loadRepositories(owner)

			override fun createCall() = apiService.getRepos(owner)

			override fun onFetchFailed() {
				repoListRateLimit.reset(owner)
			}
		}.asLiveData()
	}

	fun loadRepo(owner: String, name: String): LiveData<Resource<Repo>> {
		return object : NetworkBoundResource<Repo, Repo>(appExecutors) {
			override fun saveCallResult(item: Repo) {
				repoDao.insert(item)
			}

			override fun shouldFetch(data: Repo?) = data == null

			override fun loadFromDb() = repoDao.load(
				ownerLogin = owner,
				name = name
			)

			override fun createCall() = apiService.getRepo(
				owner = owner,
				name = name
			)
		}.asLiveData()
	}

	fun loadContributors(owner: String, name: String): LiveData<Resource<List<Contributor>>> {
		return object : NetworkBoundResource<List<Contributor>, List<Contributor>>(appExecutors) {
			override fun saveCallResult(item: List<Contributor>) {
				item.forEach {
					it.repoName = name
					it.repoOwner = owner
				}
				db.runInTransaction {
					repoDao.createRepoIfNotExists(
						Repo(
							id = Repo.UNKNOWN_ID,
							name = name,
							fullName = "$owner/$name",
							description = "",
							owner = Repo.Owner(owner, null),
							stars = 0
						)
					)
					repoDao.insertContributors(item)
				}
			}

			override fun shouldFetch(data: List<Contributor>?): Boolean {
				return data == null || data.isEmpty()
			}

			override fun loadFromDb() = repoDao.loadContributors(owner, name)

			override fun createCall() = apiService.getContributors(owner, name)
		}.asLiveData()
	}

	fun searchNextPage(query: String): LiveData<Resource<Boolean>> {
		val fetchNextSearchPageTask = FetchNextSearchPageTask(
			query = query,
			apiService = apiService,
			db = db
		)
		appExecutors.networkIO().execute(fetchNextSearchPageTask)
		return fetchNextSearchPageTask.liveData
	}

	fun search(query: String): LiveData<Resource<List<Repo>>> {
		return object : NetworkBoundResource<List<Repo>, RepoSearchResponse>(appExecutors) {

			override fun saveCallResult(item: RepoSearchResponse) {
				val repoIds = item.items.map { it.id }
				val repoSearchResult = RepoSearchResult(
					query = query,
					repoIds = repoIds,
					totalCount = item.total,
					next = item.nextPage
				)
				db.beginTransaction()
				try {
					repoDao.insertRepos(item.items)
					repoDao.insert(repoSearchResult)
					db.setTransactionSuccessful()
				} finally {
					db.endTransaction()
				}
			}

			override fun shouldFetch(data: List<Repo>?) = data == null

			override fun loadFromDb(): LiveData<List<Repo>> {
				return Transformations.switchMap(repoDao.search(query)) { searchData ->
					if (searchData == null) {
						AbsentLiveData.create()
					} else {
						repoDao.loadOrdered(searchData.repoIds)
					}
				}
			}

			override fun createCall() = apiService.searchRepos(query)

			override fun processResponse(response: ApiSuccessResponse<RepoSearchResponse>)
					: RepoSearchResponse {
				val body = response.body
				body.nextPage = response.nextPage
				return body
			}
		}.asLiveData()
	}
}
