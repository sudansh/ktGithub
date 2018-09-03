package com.sudansh.github.api

import com.google.gson.annotations.SerializedName
import com.sudansh.github.vo.Repo

/**
 * Simple object to hold repo search responses. This is different from the Entity in the database
 * because we are keeping a search result in 1 row and denormalizing list of results into a single
 * column.
 */
data class RepoSearchResponse(
	@SerializedName("total_count")
	val total: Int = 0,
	@SerializedName("items")
	val items: List<Repo>
) {
	var nextPage: Int? = null
}
