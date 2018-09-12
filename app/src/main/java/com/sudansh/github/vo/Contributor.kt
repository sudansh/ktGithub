package com.sudansh.github.vo

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import com.google.gson.annotations.SerializedName

@Entity(
	primaryKeys = ["repoName", "repoOwner", "login"],
	foreignKeys = [ForeignKey(
		entity = Repo::class,
		parentColumns = ["name", "owner_login"],
		childColumns = ["repoName", "repoOwner"],
		onUpdate = ForeignKey.CASCADE,
		deferred = true
	)]
)
data class Contributor(
	@SerializedName("login")
	val login: String,
	@SerializedName("contributions")
	val contributions: Int,
	@SerializedName("avatar_url")
	val avatarUrl: String?
) {

	// does not show up in the response but set in post processing.
	lateinit var repoName: String
	// does not show up in the response but set in post processing.
	lateinit var repoOwner: String
}
