package com.sudansh.github.util

import com.sudansh.github.vo.Contributor
import com.sudansh.github.vo.Repo
import com.sudansh.github.vo.User

object TestUtil {

    fun createUser(login: String) = User(
        login = login,
        avatarUrl = null,
        name = "$login name",
        company = null,
        reposUrl = null,
        blog = null
    )

    fun createRepos(count: Int, owner: String, name: String, description: String): List<Repo> {
        return (0 until count).map {
            createRepo(
                owner = owner + it,
                name = name + it,
                description = description + it
            )
        }
    }

    fun createRepo(owner: String, name: String, description: String) = createRepo(
        id = Repo.UNKNOWN_ID,
        owner = owner,
        name = name,
        description = description
    )

    fun createRepo(id: Int, owner: String, name: String, description: String) = Repo(
        id = id,
        name = name,
        fullName = "$owner/$name",
        description = description,
        owner = Repo.Owner(owner, null),
        stars = 3
    )

    fun createContributor(repo: Repo, login: String, contributions: Int) = Contributor(
        login = login,
        contributions = contributions,
        avatarUrl = null
    ).apply {
        repoName = repo.name
        repoOwner = repo.owner.login
    }
}
