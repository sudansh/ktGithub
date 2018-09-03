package com.sudansh.github.util

import android.app.Activity
import android.support.v4.app.Fragment
import com.sudansh.github.di.ACTIVITY_PARAM
import org.koin.android.ext.android.inject

inline fun <reified T> Activity.injectActivity(): Lazy<T> =
	inject(parameters = { mapOf(ACTIVITY_PARAM to this) })

inline fun <reified T> Fragment.injectActivity(): Lazy<T> =
	inject(parameters = { mapOf(ACTIVITY_PARAM to requireActivity()) })