package com.sudansh.github.vo

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import com.google.gson.annotations.SerializedName

/**
 * Using name/owner_login as primary key instead of id since name/owner_login is always available
 * vs id is not.
 */
@Entity(
	indices = [
		Index("id"),
		Index("owner_login")],
	primaryKeys = ["name", "owner_login"]
)
data class Repo(
	val id: Int,
	@SerializedName("name")
	val name: String,
	@SerializedName("full_name")
	val fullName: String,
	@SerializedName("description")
	val description: String?,
	@SerializedName("owner")
	@Embedded(prefix = "owner_")
	val owner: Owner,
	@SerializedName("stargazers_count")
	val stars: Int
) {

	data class Owner(
		@SerializedName("login")
		val login: String,
		@SerializedName("url")
		val url: String?
	)

	companion object {
		const val UNKNOWN_ID = -1
	}
}
