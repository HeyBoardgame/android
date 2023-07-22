package com.project.heyboardgame.retrofit

import com.project.heyboardgame.dataStore.MyDataStore
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber

class TokenInterceptor(private val dataStore: MyDataStore) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = runBlocking { dataStore.getAccessToken() }

        val requestBuilder = chain.request().newBuilder()

        // accessToken이 존재하면 헤더에 추가
        if (accessToken.isNotEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $accessToken")
        }

        val request = requestBuilder.build()

        var response = chain.proceed(request) // 헤더만 붙여서 기존 요청 보내기

        // 만료된 accessToken으로 인증되지 않은 경우 refreshToken으로 accessToken 재발급
        if (response.code == 401) {
            val refreshToken = runBlocking { dataStore.getRefreshToken() }

            if (refreshToken.isNotEmpty()) {
                val api = RetrofitClient.getInstance(dataStore).create(Api::class.java)

                // refreshToken을 사용하여 accessToken 재발급하는 API 요청 수행
                val refreshResponse = runBlocking { api.getNewToken(refreshToken) }

                if (refreshResponse.isSuccessful) {
                    // 재발급된 accessToken 가져오기
                    val refreshResult = refreshResponse.body()
                    refreshResult?.let {
                        val newAccessToken = it.result.accessToken
                        val newRefreshToken = it.result.refreshToken

                        runBlocking {
                            dataStore.setAccessToken(newAccessToken)
                            dataStore.setRefreshToken(newRefreshToken)
                        }

                        val newRequest = request.newBuilder()
                            .header("Authorization", "Bearer $newAccessToken")
                            .build()

                        response = chain.proceed(newRequest)
                    }
                } else {
                    Timber.d("refresh 실패")
                }
            }
        }
        return response
    }
}