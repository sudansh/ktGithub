package com.android.example.github.di

import android.app.Application
import android.arch.persistence.room.Room
import com.android.example.github.api.GithubService
import com.android.example.github.db.AppDatabase
import com.android.example.github.db.RepoDao
import com.android.example.github.db.UserDao
import com.android.example.github.util.LiveDataCallAdapterFactory
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class AppModule {
    @Singleton
    @Provides
    fun provideGithubService(): GithubService {
        return Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .build()
            .create(GithubService::class.java)
    }

    @Singleton
    @Provides
    fun provideDb(app: Application): AppDatabase {
        return Room.databaseBuilder(app, AppDatabase::class.java, "github.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideUserDao(db: AppDatabase): UserDao {
        return db.userDao()
    }

    @Singleton
    @Provides
    fun provideRepoDao(db: AppDatabase): RepoDao {
        return db.repoDao()
    }
}
