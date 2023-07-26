package com.project.heyboardgame.retrofit

import com.project.heyboardgame.dataModel.RefreshData
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
        val response = chain.proceed(request) // 헤더만 붙여서 기존 요청 보내기

        // 만료된 accessToken으로 인증되지 않은 경우 refreshToken으로 accessToken 재발급
        if (response.code == 401) {
            var newAccessToken = ""
            val refreshToken = runBlocking { dataStore.getRefreshToken() }

            if (refreshToken.isNotEmpty()) {
                val api = RetrofitClient.getInstanceWithoutTokenInterceptor(dataStore).create(Api::class.java)
                val refreshData = RefreshData(accessToken, refreshToken)

                runBlocking {
                    val refreshResponse = api.getNewToken(refreshData)

                    if (refreshResponse.isSuccessful) {
                        // 재발급된 accessToken 가져오기
                        val refreshResult = refreshResponse.body()
                        refreshResult?.let {
                            newAccessToken = it.result.accessToken
                            val newRefreshToken = it.result.refreshToken

                            dataStore.setAccessToken(newAccessToken)
                            dataStore.setRefreshToken(newRefreshToken)
                        }
                    } else {
                        Timber.d("refresh 실패")
                    }
                }
                response.close()

                val newRequest = request.newBuilder()
                    .header("Authorization", "Bearer $newAccessToken")
                    .build()
                return chain.proceed(newRequest)
            }
        }
        return response
    }
}