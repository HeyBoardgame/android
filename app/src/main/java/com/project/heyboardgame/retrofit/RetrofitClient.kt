package com.project.heyboardgame.retrofit

import com.project.heyboardgame.dataStore.MyDataStore
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://13.125.211.203:8080"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // TokenInterceptor 생성
    private fun createTokenInterceptor(dataStore: MyDataStore): TokenInterceptor {
        return TokenInterceptor(dataStore)
    }

    // OkHttpClient에 TokenInterceptor 추가
    private fun createHttpClient(dataStore: MyDataStore): OkHttpClient {
        val tokenInterceptor = createTokenInterceptor(dataStore)
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(tokenInterceptor)
            .build()
    }

    // Retrofit 객체 생성
    private fun createRetrofit(dataStore: MyDataStore): Retrofit {
        val httpClient = createHttpClient(dataStore)
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getInstance(dataStore: MyDataStore): Retrofit {
        return createRetrofit(dataStore)
    }
}