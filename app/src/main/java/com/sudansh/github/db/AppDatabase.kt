package com.sudansh.github.db


import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.sudansh.github.vo.Contributor
import com.sudansh.github.vo.Repo
import com.sudansh.github.vo.RepoSearchResult
import com.sudansh.github.vo.User

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
