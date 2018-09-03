package com.sudansh.github.ui.common

import com.sudansh.github.MainActivity
import com.sudansh.github.R
import com.sudansh.github.testing.OpenForTesting
import com.sudansh.github.ui.repo.RepoFragment
import com.sudansh.github.ui.search.SearchFragment
import com.sudansh.github.ui.user.UserFragment

/**
 * A utility class that handles navigation in [MainActivity].
 */
@OpenForTesting
class NavigationController(mainActivity: MainActivity) {
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
