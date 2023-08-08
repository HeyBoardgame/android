package com.project.heyboardgame.retrofit

import com.project.heyboardgame.dataStore.MyDataStore
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://13.125.211.203:8080/api/v1/"

    // Singleton으로 관리할 TokenInterceptor 인스턴스
    private var tokenInterceptor: TokenInterceptor? = null

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // TokenInterceptor 생성 또는 기존 인스턴스 반환
    private fun getTokenInterceptor(dataStore: MyDataStore): TokenInterceptor {
        if (tokenInterceptor == null) {
            tokenInterceptor = TokenInterceptor(dataStore)
        }
        return tokenInterceptor!!
    }

    // OkHttpClient에 TokenInterceptor 추가
    private fun createHttpClient(dataStore: MyDataStore, includeTokenInterceptor: Boolean): OkHttpClient {
        val httpClientBuilder = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)

        if (includeTokenInterceptor) {
            val tokenInterceptor = getTokenInterceptor(dataStore)
            httpClientBuilder.addInterceptor(tokenInterceptor)
        }

        return httpClientBuilder.build()
    }

    // Retrofit 객체 생성
    private fun createRetrofit(dataStore: MyDataStore, includeTokenInterceptor: Boolean): Retrofit {
        val httpClient = createHttpClient(dataStore, includeTokenInterceptor)
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // 특정 요청에 대해 TokenInterceptor를 포함하지 않도록 Retrofit 객체 생성
    fun getInstanceWithoutTokenInterceptor(dataStore: MyDataStore): Retrofit {
        return createRetrofit(dataStore, includeTokenInterceptor = false)
    }

    // 기본적으로 모든 요청에 TokenInterceptor를 포함하는 Retrofit 객체 생성
    fun getInstanceWithTokenInterceptor(dataStore: MyDataStore): Retrofit {
        return createRetrofit(dataStore, includeTokenInterceptor = true)
    }
}