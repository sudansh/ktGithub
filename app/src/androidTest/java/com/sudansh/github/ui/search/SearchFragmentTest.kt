package com.sudansh.github.ui.search

import android.arch.lifecycle.MutableLiveData
import android.databinding.DataBindingComponent
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.v7.widget.RecyclerView
import android.view.KeyEvent
import com.sudansh.github.R
import com.sudansh.github.binding.FragmentBindingAdapters
import com.sudansh.github.testing.SingleFragmentActivity
import com.sudansh.github.ui.common.NavigationController
import com.sudansh.github.util.*
import com.sudansh.github.vo.Repo
import com.sudansh.github.vo.Resource
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*

@RunWith(AndroidJUnit4::class)
class SearchFragmentTest {
    @get:Rule
    val activityRule = ActivityTestRule(SingleFragmentActivity::class.java, true, true)
    @get:Rule
    val executorRule = TaskExecutorWithIdlingResourceRule()
    @get:Rule
    val countingAppExecutors = CountingAppExecutorsRule()

    private lateinit var mockBindingAdapter: FragmentBindingAdapters
    private lateinit var navigationController: NavigationController
    private lateinit var viewModel: SearchViewModel
    private val results = MutableLiveData<Resource<List<Repo>>>()
    private val loadMoreStatus = MutableLiveData<SearchViewModel.LoadMoreState>()

    @Before
    fun init() {
        EspressoTestUtil.disableProgressBarAnimations(activityRule)
        val searchFragment = SearchFragment()
        viewModel = mock(SearchViewModel::class.java)
        doReturn(loadMoreStatus).`when`(viewModel).loadMoreStatus
        `when`(viewModel.results).thenReturn(results)

        mockBindingAdapter = mock(FragmentBindingAdapters::class.java)
        navigationController = mock(NavigationController::class.java)

        searchFragment.appExecutors = countingAppExecutors.appExecutors
        searchFragment.viewModelFactory = ViewModelUtil.createFor(viewModel)
        searchFragment.dataBindingComponent = object : DataBindingComponent {
            override fun getFragmentBindingAdapters(): FragmentBindingAdapters {
                return mockBindingAdapter
            }
        }
        searchFragment.navigationController = navigationController
        activityRule.activity.setFragment(searchFragment)
    }

    @Test
    fun search() {
        onView(withId(R.id.progress_bar)).check(matches(not(isDisplayed())))
        onView(withId(R.id.input)).perform(
            typeText("foo"),
            pressKey(KeyEvent.KEYCODE_ENTER)
        )
        verify(viewModel).setQuery("foo")
        results.postValue(Resource.loading(null))
        onView(withId(R.id.progress_bar)).check(matches(isDisplayed()))
    }

    @Test
    fun loadResults() {
        val repo = TestUtil.createRepo("foo", "bar", "desc")
        results.postValue(Resource.success(arrayListOf(repo)))
        onView(listMatcher().atPosition(0)).check(matches(hasDescendant(withText("foo/bar"))))
        onView(withId(R.id.progress_bar)).check(matches(not(isDisplayed())))
    }

    @Test
    fun dataWithLoading() {
        val repo = TestUtil.createRepo("foo", "bar", "desc")
        results.postValue(Resource.loading(arrayListOf(repo)))
        onView(listMatcher().atPosition(0)).check(matches(hasDescendant(withText("foo/bar"))))
        onView(withId(R.id.progress_bar)).check(matches(not(isDisplayed())))
    }

    @Test
    fun error() {
        results.postValue(Resource.error("failed to load", null))
        onView(withId(R.id.error_msg)).check(matches(isDisplayed()))
    }

    @Test
    fun loadMore() {
        val repos = TestUtil.createRepos(50, "foo", "barr", "desc")
        results.postValue(Resource.success(repos))
        val action = RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(49)
        onView(withId(R.id.repo_list)).perform(action)
        onView(listMatcher().atPosition(49)).check(matches(isDisplayed()))
        verify(viewModel).loadNextPage()
    }

    @Test
    fun navigateToRepo() {
        doNothing().`when`<SearchViewModel>(viewModel).loadNextPage()
        val repo = TestUtil.createRepo("foo", "bar", "desc")
        results.postValue(Resource.success(arrayListOf(repo)))
        onView(withText("desc")).perform(click())
        verify(navigationController).navigateToRepo("foo", "bar")
    }

    @Test
    fun loadMoreProgress() {
        loadMoreStatus.postValue(SearchViewModel.LoadMoreState(true, null))
        onView(withId(R.id.load_more_bar)).check(matches(isDisplayed()))
        loadMoreStatus.postValue(SearchViewModel.LoadMoreState(false, null))
        onView(withId(R.id.load_more_bar)).check(matches(not(isDisplayed())))
    }

    @Test
    fun loadMoreProgressError() {
        loadMoreStatus.postValue(SearchViewModel.LoadMoreState(true, "QQ"))
        onView(withText("QQ")).check(
            matches(
                withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)
            )
        )
    }

    private fun listMatcher(): RecyclerViewMatcher {
        return RecyclerViewMatcher(R.id.repo_list)
    }
}