package com.sudansh.github

import android.app.Application

/**
 * We use a separate App for tests to prevent initializing dependency injection.
 *
 * See [com.sudansh.github.util.AppTestRunner].
 */
class TestApp : Application()
