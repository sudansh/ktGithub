package com.sudansh.github.api

import android.arch.lifecycle.LiveData
import com.sudansh.github.vo.Contributor
import com.sudansh.github.vo.Repo
import com.sudansh.github.vo.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * REST API access points
 */
interface ApiService {
	@GET("users/{login}")
	fun getUser(@Path("login") login: String): LiveData<ApiResponse<User>>

	@GET("users/{login}/repos")
	fun getRepos(@Path("login") login: String): LiveData<ApiResponse<List<Repo>>>

	@GET("repos/{owner}/{name}")
	fun getRepo(
		@Path("owner") owner: String,
		@Path("name") name: String
	): LiveData<ApiResponse<Repo>>

	@GET("repos/{owner}/{name}/contributors")
	fun getContributors(
		@Path("owner") owner: String,
		@Path("name") name: String
	): LiveData<ApiResponse<List<Contributor>>>

	@GET("search/repositories")
	fun searchRepos(@Query("q") query: String): LiveData<ApiResponse<RepoSearchResponse>>

	@GET("search/repositories")
	fun searchRepos(@Query("q") query: String, @Query("page") page: Int): Call<RepoSearchResponse>
}
