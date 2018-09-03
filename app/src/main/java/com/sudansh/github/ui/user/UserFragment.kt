package com.sudansh.github.ui.user

import android.arch.lifecycle.Observer
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
import com.sudansh.github.databinding.UserFragmentBinding
import com.sudansh.github.ui.common.NavigationController
import com.sudansh.github.ui.common.RepoListAdapter
import com.sudansh.github.ui.common.RetryCallback
import com.sudansh.github.util.injectActivity
import org.koin.android.ext.android.inject

class UserFragment : Fragment() {

    val navigationController: NavigationController by injectActivity()
    val appExecutors: AppExecutors by inject()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    lateinit var binding: UserFragmentBinding
    lateinit var adapter: RepoListAdapter
    val userViewModel: UserViewModel by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<UserFragmentBinding>(
            inflater,
            R.layout.user_fragment,
            container,
            false,
            dataBindingComponent
        )
        dataBinding.retryCallback = object : RetryCallback {
            override fun retry() {
                userViewModel.retry()
            }
        }
        binding = dataBinding
        return dataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        userViewModel.setLogin(arguments?.getString(LOGIN_KEY))

        userViewModel.user.observe(this, Observer { userResource ->
            binding.user = userResource?.data
            binding.userResource = userResource
            // this is only necessary because espresso cannot read data binding callbacks.
            binding.executePendingBindings()
        })
        val rvAdapter = RepoListAdapter(
            dataBindingComponent = dataBindingComponent,
            appExecutors = appExecutors
        ) { repo ->
            navigationController.navigateToRepo(
                owner = repo.owner.login,
                name = repo.name
            )
        }
        binding.repoList.adapter = rvAdapter
        this.adapter = rvAdapter
        initRepoList()
    }

    private fun initRepoList() {
        userViewModel.repositories.observe(this, Observer { repos ->
            // no null checks for adapter since LiveData guarantees that we'll not receive
            // the event if fragment is now show.
            adapter.submitList(repos?.data)
        })
    }

    companion object {
        private const val LOGIN_KEY = "login"

        fun create(login: String): UserFragment {
            val userFragment = UserFragment()
            val bundle = Bundle()
            bundle.putString(LOGIN_KEY, login)
            userFragment.arguments = bundle
            return userFragment
        }
    }
}
