package com.sudansh.github.di

import android.arch.persistence.room.Room
import com.sudansh.github.AppExecutors
import com.sudansh.github.db.AppDatabase
import org.koin.dsl.module.applicationContext

val localModule = applicationContext {
    bean { AppExecutors() }
    bean {
        Room.databaseBuilder(get(), AppDatabase::class.java, "github-db").build()
    }
    bean { get<AppDatabase>().userDao() }
    bean { get<AppDatabase>().repoDao() }
}