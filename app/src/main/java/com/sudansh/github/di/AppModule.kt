package com.sudansh.github.di

import com.sudansh.github.AppExecutors
import com.sudansh.github.repository.RepoRepository
import com.sudansh.github.repository.UserRepository
import com.sudansh.github.ui.common.NavigationController
import com.sudansh.github.ui.repo.RepoViewModel
import com.sudansh.github.ui.search.SearchViewModel
import com.sudansh.github.ui.user.UserViewModel
import org.koin.android.architecture.ext.viewModel
import org.koin.dsl.module.applicationContext

const val ACTIVITY_PARAM: String = "activity"
val appModule = applicationContext {

	bean { NavigationController(get()) }
	bean { RepoRepository(get(), get(), get(), get()) }
	bean { UserRepository(get(), get(), get()) }
	bean { AppExecutors() }

	viewModel { SearchViewModel(get()) }
	viewModel { UserViewModel(get(), get()) }
	viewModel { RepoViewModel(get()) }

	factory { params -> NavigationController(params[ACTIVITY_PARAM]) }
}
