package com.sudansh.github.db


import android.arch.core.executor.testing.CountingTaskExecutorRule
import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import org.junit.After
import org.junit.Before
import org.junit.Rule
import java.util.concurrent.TimeUnit

abstract class DbTest {
    @get:Rule
    val countingTaskExecutorRule = CountingTaskExecutorRule()
    private lateinit var _db: AppDatabase
    val db: AppDatabase
        get() = _db

    @Before
    fun initDb() {
        _db = Room.inMemoryDatabaseBuilder(
                InstrumentationRegistry.getContext(),
                AppDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() {
        countingTaskExecutorRule.drainTasks(10, TimeUnit.SECONDS)
        _db.close()
    }
}
