package com.android.example.github.db


import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.android.example.github.vo.Contributor
import com.android.example.github.vo.Repo
import com.android.example.github.vo.RepoSearchResult
import com.android.example.github.vo.User

/**
 * Main database description.
 */
@Database(
    entities = [
        User::class,
        Repo::class,
        Contributor::class,
        RepoSearchResult::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun repoDao(): RepoDao
}
