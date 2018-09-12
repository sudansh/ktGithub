package com.sudansh.github.vo

import android.arch.persistence.room.Entity
import com.google.gson.annotations.SerializedName

@Entity(primaryKeys = ["login"])
data class User(
	@SerializedName("login")
	val login: String,
	@SerializedName("avatar_url")
	val avatarUrl: String?,
	@SerializedName("name")
	val name: String?,
	@SerializedName("company")
	val company: String?,
	@SerializedName("repos_url")
	val reposUrl: String?,
	@SerializedName("blog")
	val blog: String?
)
