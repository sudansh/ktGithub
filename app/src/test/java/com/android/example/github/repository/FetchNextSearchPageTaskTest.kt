package com.android.example.github.repository

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import com.android.example.github.api.ApiService
import com.android.example.github.api.RepoSearchResponse
import com.android.example.github.db.AppDatabase
import com.android.example.github.db.RepoDao
import com.android.example.github.util.TestUtil
import com.android.example.github.util.mock
import com.android.example.github.vo.RepoSearchResult
import com.android.example.github.vo.Resource
import okhttp3.Headers
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.*
import retrofit2.Call
import retrofit2.Response
import java.io.IOException

@RunWith(JUnit4::class)
class FetchNextSearchPageTaskTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var service: ApiService

    private lateinit var db: AppDatabase

    private lateinit var repoDao: RepoDao

    private lateinit var task: FetchNextSearchPageTask

    private val observer: Observer<Resource<Boolean>> = mock()

    @Before
    fun init() {
        service = mock(ApiService::class.java)
        db = mock(AppDatabase::class.java)
        repoDao = mock(RepoDao::class.java)
        `when`(db.repoDao()).thenReturn(repoDao)
        task = FetchNextSearchPageTask("foo", service, db)
        task.liveData.observeForever(observer)
    }

    @Test
    fun withoutResult() {
        `when`(repoDao.search("foo")).thenReturn(null)
        task.run()
        verify(observer).onChanged(null)
        verifyNoMoreInteractions(observer)
        verifyNoMoreInteractions(service)
    }

    @Test
    fun noNextPage() {
        createDbResult(null)
        task.run()
        verify(observer).onChanged(Resource.success(false))
        verifyNoMoreInteractions(observer)
        verifyNoMoreInteractions(service)
    }

    @Test
    fun nextPageWithNull() {
        createDbResult(1)
        val repos = TestUtil.createRepos(10, "a", "b", "c")
        val result = RepoSearchResponse(10, repos)
        val call = createCall(result, null)
        `when`(service.searchRepos("foo", 1)).thenReturn(call)
        task.run()
        verify(repoDao).insertRepos(repos)
        verify(observer).onChanged(Resource.success(false))
    }

    @Test
    fun nextPageWithMore() {
        createDbResult(1)
        val repos = TestUtil.createRepos(10, "a", "b", "c")
        val result = RepoSearchResponse(10, repos)
        result.nextPage = 2
        val call = createCall(result, 2)
        `when`(service.searchRepos("foo", 1)).thenReturn(call)
        task.run()
        verify(repoDao).insertRepos(repos)
        verify(observer).onChanged(Resource.success(true))
    }

    @Test
    fun nextPageApiError() {
        createDbResult(1)
        val call = mock<Call<RepoSearchResponse>>()
        `when`(call.execute()).thenReturn(
            Response.error(
                400, ResponseBody.create(
                    MediaType.parse("txt"), "bar"
                )
            )
        )
        `when`(service.searchRepos("foo", 1)).thenReturn(call)
        task.run()
        verify(observer)!!.onChanged(Resource.error("bar", true))
    }

    @Test
    fun nextPageIOError() {
        createDbResult(1)
        val call = mock<Call<RepoSearchResponse>>()
        `when`(call.execute()).thenThrow(IOException("bar"))
        `when`(service.searchRepos("foo", 1)).thenReturn(call)
        task.run()
        verify(observer)!!.onChanged(Resource.error("bar", true))
    }

    private fun createDbResult(nextPage: Int?) {
        val result = RepoSearchResult(
            "foo", emptyList(),
            0, nextPage
        )
        `when`(repoDao.findSearchResult("foo")).thenReturn(result)
    }

    private fun createCall(body: RepoSearchResponse, nextPage: Int?): Call<RepoSearchResponse> {
        val headers = if (nextPage == null)
            null
        else
            Headers
                .of(
                    "link",
                    "<https://api.github.com/search/repositories?q=foo&page=" + nextPage
                            + ">; rel=\"next\""
                )
        val success = if (headers == null)
            Response.success(body)
        else
            Response.success(body, headers)
        val call = mock<Call<RepoSearchResponse>>()
        `when`(call.execute()).thenReturn(success)

        return call
    }
}