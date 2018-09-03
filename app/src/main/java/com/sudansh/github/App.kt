package com.sudansh.github

import android.app.Application
import com.sudansh.github.di.appModule
import com.sudansh.github.di.localModule
import com.sudansh.github.di.remoteModule
import org.koin.android.ext.android.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
		startKoin(listOf(appModule,
						 localModule,
						 remoteModule))
    }

}
