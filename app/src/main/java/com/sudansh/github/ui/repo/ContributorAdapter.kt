package com.sudansh.github.ui.repo

import android.databinding.DataBindingComponent
import android.databinding.DataBindingUtil
import android.support.v7.util.DiffUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import com.sudansh.github.AppExecutors
import com.sudansh.github.R
import com.sudansh.github.databinding.ContributorItemBinding
import com.sudansh.github.ui.common.DataBoundListAdapter
import com.sudansh.github.vo.Contributor

class ContributorAdapter(
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors,
    private val callback: ((Contributor) -> Unit)?
) : DataBoundListAdapter<Contributor, ContributorItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<Contributor>() {
        override fun areItemsTheSame(oldItem: Contributor, newItem: Contributor): Boolean {
            return oldItem.login == newItem.login
        }

        override fun areContentsTheSame(oldItem: Contributor, newItem: Contributor): Boolean {
            return oldItem.avatarUrl == newItem.avatarUrl
                    && oldItem.contributions == newItem.contributions
        }
    }
) {

    override fun createBinding(parent: ViewGroup): ContributorItemBinding {
        val binding = DataBindingUtil
            .inflate<ContributorItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.contributor_item,
                parent,
                false,
                dataBindingComponent
            )
        binding.root.setOnClickListener {
            binding.contributor?.let {
                callback?.invoke(it)
            }
        }
        return binding
    }

    override fun bind(binding: ContributorItemBinding, item: Contributor) {
        binding.contributor = item
    }
}
