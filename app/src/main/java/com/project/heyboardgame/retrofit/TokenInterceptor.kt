import com.project.heyboardgame.dataStore.MyDataStore
import com.project.heyboardgame.retrofit.ApiService
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor(private val dataStore: MyDataStore) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = runBlocking { dataStore.getAccessToken() }

        val requestBuilder = chain.request().newBuilder()

        // accessToken이 존재하면 헤더에 추가
        if (accessToken.isNotEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $accessToken")
        }

        val request = requestBuilder.build()

        var response = chain.proceed(request)

        // 만료된 accessToken으로 인증되지 않은 경우 refreshToken으로 accessToken 재발급
        if (response.code == 401) {
            val refreshToken = runBlocking { dataStore.getRefreshToken() }

            if (refreshToken.isNotEmpty()) {
                val apiService = RetrofitClient.getInstance(dataStore).create(ApiService::class.java)

                // refreshToken을 사용하여 accessToken 재발급하는 API 요청 수행
                val refreshResponse = runBlocking { apiService.getNewToken(refreshToken).execute() }

                if (refreshResponse.isSuccessful) {
                    // 재발급된 accessToken 가져오기
                    val newAccessToken = refreshResponse.body()

                    if (newAccessToken != null) {
                        // 재발급된 accessToken을 dataStore에 저장
                        runBlocking { dataStore.setAccessToken(newAccessToken) }

                        // 재발급된 accessToken으로 기존 요청 다시 실행
                        val newRequest = request.newBuilder()
                            .header("Authorization", "Bearer $newAccessToken")
                            .build()

                        response.close()
                        response = chain.proceed(newRequest)
                    }
                }
            }
        }

        return response
    }
}