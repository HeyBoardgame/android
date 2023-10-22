package com.project.heyboardgame.websocket

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.project.heyboardgame.App
import com.project.heyboardgame.auth.AuthActivity
import com.project.heyboardgame.dataModel.RefreshData
import com.project.heyboardgame.dataStore.MyDataStore
import com.project.heyboardgame.retrofit.Api
import com.project.heyboardgame.retrofit.RetrofitClient
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber

class MyWebSocketListener(private val webSocketCallback: WebSocketCallback) : WebSocketListener() {
    private val dataStore = MyDataStore()
    private val appContext = App.getContext()


    override fun onOpen(webSocket: WebSocket, response: Response) {
        Timber.d("WebSocket connected")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        Timber.d("Received message: $text")
        try {
            val jsonObject = JSONObject(text)

            if (jsonObject.has("sender")) {
                val senderId = jsonObject.getInt("sender")
                val message = jsonObject.getString("msg")
                val timestamp = jsonObject.getString("timeStamp")
                webSocketCallback.onMessageReceived(senderId, message, timestamp)
            }

        } catch (e: JSONException) {
            Timber.e(e, "Error parsing JSON message")
        }
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        Timber.d("WebSocket closed")
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Timber.e(t, "WebSocket failure")
        if (response != null && response.code == 401) {
            // 액세스 토큰이 만료되었을 때 처리
            refreshTokenAndReconnect(webSocket, response)
        }
    }

    private fun refreshTokenAndReconnect(webSocket: WebSocket, response: Response) {
        if (response.code == 401) {
            val accessToken = runBlocking { dataStore.getAccessToken() }
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

                        val request = Request.Builder()
                            .url(webSocket.request().url)
                            .header("Authorization", "Bearer $newAccessToken")
                            .build()

                        val newWebSocketListener = MyWebSocketListener(webSocketCallback)
                        val newOkHttpClient = OkHttpClient()
                        val newWebSocket = newOkHttpClient.newWebSocket(request, newWebSocketListener)
                        newOkHttpClient.dispatcher.executorService.shutdown()
                    }
                } else {
                    Timber.d("refresh 실패")
                    redirectToLoginScreen(appContext)
                }
            }
        }
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