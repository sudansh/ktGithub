package com.sudansh.github.di

import com.sudansh.github.api.ApiService
import com.sudansh.github.util.LiveDataCallAdapterFactory
import okhttp3.OkHttpClient
import org.koin.dsl.module.applicationContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val remoteModule = applicationContext {
    bean { createOkHttpClient() }
    bean { createWebService<ApiService>(get()) }
}

fun createOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder().apply {
        connectTimeout(60L, TimeUnit.SECONDS)
        readTimeout(60L, TimeUnit.SECONDS)
    }.build()
}

inline fun <reified T> createWebService(okHttpClient: OkHttpClient): T {
    return Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .client(okHttpClient)
            .build()
            .create(T::class.java)
}