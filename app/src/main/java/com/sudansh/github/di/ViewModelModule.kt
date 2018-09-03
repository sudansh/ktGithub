package com.sudansh.github.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider

import com.sudansh.github.ui.repo.RepoViewModel
import com.sudansh.github.ui.search.SearchViewModel
import com.sudansh.github.ui.user.UserViewModel
import com.sudansh.github.viewmodel.AppViewModelFactory

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Suppress("unused")
@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(UserViewModel::class)
    abstract fun bindUserViewModel(userViewModel: UserViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel::class)
    abstract fun bindSearchViewModel(searchViewModel: SearchViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(RepoViewModel::class)
    abstract fun bindRepoViewModel(repoViewModel: RepoViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: AppViewModelFactory): ViewModelProvider.Factory
}
