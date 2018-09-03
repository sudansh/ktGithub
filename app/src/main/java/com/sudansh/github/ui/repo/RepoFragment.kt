package com.sudansh.github.ui.repo

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingComponent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sudansh.github.AppExecutors
import com.sudansh.github.R
import com.sudansh.github.binding.FragmentDataBindingComponent
import com.sudansh.github.databinding.RepoFragmentBinding
import com.sudansh.github.di.Injectable
import com.sudansh.github.ui.common.NavigationController
import com.sudansh.github.ui.common.RetryCallback
import javax.inject.Inject

/**
 * The UI Controller for displaying a Github Repo's information with its contributors.
 */
class RepoFragment : Fragment(), Injectable {

	@Inject
	lateinit var viewModelFactory: ViewModelProvider.Factory

	lateinit var repoViewModel: RepoViewModel

	@Inject
	lateinit var navigationController: NavigationController

	@Inject
	lateinit var appExecutors: AppExecutors

	// mutable for testing
	var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
	lateinit var binding: RepoFragmentBinding
	private lateinit var adapter: ContributorAdapter

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		repoViewModel = ViewModelProviders.of(this, viewModelFactory)
			.get(RepoViewModel::class.java)
		val args = arguments
		val ownerParam = args?.getString(REPO_OWNER_KEY)
		val repoParam = args?.getString(REPO_NAME_KEY)
		repoViewModel.setId(ownerParam, repoParam)

		val repo = repoViewModel.repo
		repo.observe(this, Observer { resource ->
			binding.repo = resource?.data
			binding.repoResource = resource
			binding.executePendingBindings()
		})

		val adapter = ContributorAdapter(dataBindingComponent, appExecutors) { (login) ->
			navigationController.navigateToUser(login)
		}
		this.adapter = adapter
		binding.contributorList.adapter = adapter
		initContributorList(repoViewModel)
	}

	private fun initContributorList(viewModel: RepoViewModel) {
		viewModel.contributors.observe(this, Observer { listResource ->
			// we don't need any null checks here for the adapter since LiveData guarantees that
			// it won't call us if fragment is stopped or not started.
			if (listResource?.data != null) {
				adapter.submitList(listResource.data)
			} else {
				adapter.submitList(emptyList())
			}
		})
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		val dataBinding = DataBindingUtil.inflate<RepoFragmentBinding>(
			inflater,
			R.layout.repo_fragment,
			container,
			false
		)
		dataBinding.retryCallback = object : RetryCallback {
			override fun retry() {
				repoViewModel.retry()
			}
		}
		binding = dataBinding
		return dataBinding.root
	}

	companion object {

		private const val REPO_OWNER_KEY = "repo_owner"

		private const val REPO_NAME_KEY = "repo_name"

		fun create(owner: String, name: String): RepoFragment {
			val repoFragment = RepoFragment()
			val args = Bundle()
			args.putString(REPO_OWNER_KEY, owner)
			args.putString(REPO_NAME_KEY, name)
			repoFragment.arguments = args
			return repoFragment
		}
	}
}
