package com.sudansh.github.db

import android.arch.persistence.room.TypeConverter
import android.util.Log

object GithubTypeConverters {
	@TypeConverter
	@JvmStatic
	fun stringToIntList(data: String?): List<Int>? {
		return data?.let { it ->
			it.split(",").map {
				try {
					it.toInt()
				} catch (ex: NumberFormatException) {
					Log.e("GithubConverter", "Cannot convert $it to number")
					null
				}
			}
		}?.filterNotNull()
	}

	@TypeConverter
	@JvmStatic
	fun intListToString(ints: List<Int>?): String? {
		return ints?.joinToString(",")
	}
}
