package com.sudansh.github.ui.repo

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.sudansh.github.repository.RepoRepository
import com.sudansh.github.testing.OpenForTesting
import com.sudansh.github.util.AbsentLiveData
import com.sudansh.github.vo.Contributor
import com.sudansh.github.vo.Repo
import com.sudansh.github.vo.Resource

@OpenForTesting
class RepoViewModel (repository: RepoRepository) : ViewModel() {
    private val _repoId: MutableLiveData<RepoId> = MutableLiveData()
    val repoId: LiveData<RepoId>
        get() = _repoId
    val repo: LiveData<Resource<Repo>> = Transformations
            .switchMap(_repoId) { input ->
                input.ifExists { owner, name ->
                    repository.loadRepo(owner, name)
                }
            }
    val contributors: LiveData<Resource<List<Contributor>>> = Transformations
            .switchMap(_repoId) { input ->
                input.ifExists { owner, name ->
                    repository.loadContributors(owner, name)
                }
            }

    fun retry() {
        val owner = _repoId.value?.owner
        val name = _repoId.value?.name
        if (owner != null && name != null) {
            _repoId.value = RepoId(owner, name)
        }
    }

    fun setId(owner: String?, name: String?) {
        val update = RepoId(owner, name)
        if (_repoId.value == update) {
            return
        }
        _repoId.value = update
    }

    data class RepoId(val owner: String?, val name: String?) {
        fun <T> ifExists(f: (String, String) -> LiveData<T>): LiveData<T> {
            return if (owner.isNullOrBlank() || name.isNullOrBlank()) {
                AbsentLiveData.create()
            } else {
                f(owner!!, name!!)
            }
        }
    }
}
