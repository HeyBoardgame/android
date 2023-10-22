package com.project.heyboardgame.retrofit

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.project.heyboardgame.App
import com.project.heyboardgame.auth.AuthActivity
import com.project.heyboardgame.dataModel.RefreshData
import com.project.heyboardgame.dataStore.MyDataStore
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
class TokenInterceptor(private val dataStore: MyDataStore) : Interceptor {
    private val appContext = App.getContext()
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
                val api = RetrofitClient.getInstanceWithoutTokenInterceptor(dataStore).create(Api::class.java)
                val refreshData = RefreshData(accessToken, refreshToken)


                val refreshResponse = runBlocking { api.getNewToken(refreshData) }

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

                        response.close()

                        val newRequest = request.newBuilder()
                            .header("Authorization", "Bearer $newAccessToken")
                            .build()
                        response = chain.proceed(newRequest)
                    }
                } else {
                    Timber.d("refresh 실패")
                    redirectToLoginScreen(appContext)
                }
            }
        } else if (response.code == 403) {
            redirectToLoginScreen(appContext)
        }
        return response
    }

    private fun redirectToLoginScreen(context: Context) {
        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post {
            logoutGoogle(context)
            runBlocking {
                dataStore.setAccessToken("")
                dataStore.setRefreshToken("")
            }
            Toast.makeText(context, "리프레시에 실패했습니다. 로그인 화면으로 이동합니다.", Toast.LENGTH_SHORT).show()
            val intent = Intent(context, AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }
    }

    private fun logoutGoogle(context: Context) {
        val isGoogleLogined = runBlocking { dataStore.getGoogleLogined() }
        if (isGoogleLogined) {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()

            val googleSignInClient = GoogleSignIn.getClient(context, gso)

            googleSignInClient.signOut().addOnCompleteListener {
                runBlocking {
                    dataStore.setGoogleLogined(false)
                }
            }
        }
    }
}