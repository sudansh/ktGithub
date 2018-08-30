package com.sudansh.github.util

import android.app.Application
import android.content.Context
import android.support.test.runner.AndroidJUnitRunner

import com.sudansh.github.TestApp

/**
 * Custom runner to disable dependency injection.
 */
class AppTestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader, className: String, context: Context): Application {
        return super.newApplication(cl, TestApp::class.java.name, context)
    }
}
