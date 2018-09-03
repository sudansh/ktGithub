package com.sudansh.github.di

import com.sudansh.github.ui.repo.RepoFragment
import com.sudansh.github.ui.search.SearchFragment
import com.sudansh.github.ui.user.UserFragment

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class FragmentBuildersModule {
	@ContributesAndroidInjector
	abstract fun contributeRepoFragment(): RepoFragment

	@ContributesAndroidInjector
	abstract fun contributeUserFragment(): UserFragment

	@ContributesAndroidInjector
	abstract fun contributeSearchFragment(): SearchFragment
}
