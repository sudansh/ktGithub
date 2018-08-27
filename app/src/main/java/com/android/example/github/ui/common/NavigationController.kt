package com.android.example.github.ui.common

import com.android.example.github.MainActivity
import com.android.example.github.R
import com.android.example.github.testing.OpenForTesting
import com.android.example.github.ui.repo.RepoFragment
import com.android.example.github.ui.search.SearchFragment
import com.android.example.github.ui.user.UserFragment
import javax.inject.Inject

/**
 * A utility class that handles navigation in [MainActivity].
 */
@OpenForTesting
class NavigationController @Inject constructor(mainActivity: MainActivity) {
    private val containerId = R.id.container
    private val fragmentManager = mainActivity.supportFragmentManager

    fun navigateToSearch() {
        val searchFragment = SearchFragment()
        fragmentManager.beginTransaction()
            .replace(containerId, searchFragment)
            .commitAllowingStateLoss()
    }

    fun navigateToRepo(owner: String, name: String) {
        val fragment = RepoFragment.create(owner, name)
        val tag = "repo/$owner/$name"
        fragmentManager.beginTransaction()
            .replace(containerId, fragment, tag)
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }

    fun navigateToUser(login: String) {
        val tag = "user/$login"
        val userFragment = UserFragment.create(login)
        fragmentManager.beginTransaction()
            .replace(containerId, userFragment, tag)
            .addToBackStack(null)
            .commitAllowingStateLoss()
    }
}
