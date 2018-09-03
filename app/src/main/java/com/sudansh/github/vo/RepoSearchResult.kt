package com.sudansh.github.vo

import android.arch.persistence.room.Entity
import android.arch.persistence.room.TypeConverters
import com.sudansh.github.db.GithubTypeConverters

@Entity(primaryKeys = ["query"])
@TypeConverters(GithubTypeConverters::class)
data class RepoSearchResult(
	val query: String,
	val repoIds: List<Int>,
	val totalCount: Int,
	val next: Int?
)
