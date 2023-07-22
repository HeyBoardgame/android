package com.project.heyboardgame.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.heyboardgame.dataStore.MyDataStore
import com.project.heyboardgame.retrofit.Api
import com.project.heyboardgame.retrofit.RetrofitClient
import com.project.heyboardgame.dataModel.LoginData
import com.project.heyboardgame.dataModel.LoginResult
import com.project.heyboardgame.dataModel.SignUpData
import kotlinx.coroutines.launch
import retrofit2.Response
import timber.log.Timber

class AuthViewModel : ViewModel() {
    // accessToken LiveData
    private val _token = MutableLiveData<String>()
    val token : LiveData<String>
        get() = _token
    // DataStore
    private val myDataStore : MyDataStore = MyDataStore()
    // Api
    private val api : Api = RetrofitClient.getInstance(myDataStore).create(Api::class.java)

    // accessToken가 있는 지 확인하는 함수
    fun checkAccessToken() = viewModelScope.launch {

        val getToken = myDataStore.getAccessToken()
        _token.value = getToken

        Timber.d(getToken)
    }
    // 로그인 요청 함수 (LoginFragment)
    fun requestLogin(loginData: LoginData, onSuccess: () -> Unit, onFailure: () -> Unit, onErrorAction: () -> Unit) = viewModelScope.launch {
        try {
            val response = api.requestLogin(loginData)
            if (response.isSuccessful) { // 로그인 성공
                val loginResult = response.body()
                loginResult?.let {
                    // LoginResult에서 accessToken과 refreshToken 가져오기
                    val accessToken = it.result.accessToken
                    val refreshToken = it.result.refreshToken

                    // DataStore에 저장
                    myDataStore.setAccessToken(accessToken ?: "")
                    myDataStore.setRefreshToken(refreshToken ?: "")

                    // MainActivity로 이동 + Toast 메세지
                    onSuccess.invoke()
                }
            } else { // 서버로부터 로그인 실패 응답을 받았을 때 처리하는 로직
                onFailure.invoke()
            }
        } catch (e: Exception) { // 네트워크 오류
            onErrorAction.invoke()
        }
    }
    // 임시 비밀번호 발급 요청 함수 (ForgotPwdFragment)
    fun requestTempPassword(email: String, onSuccess: () -> Unit, onFailure: () -> Unit, onErrorAction: () -> Unit) = viewModelScope.launch {
        try {
            val response = api.requestTempPassword(email)
            if (response.isSuccessful) { // 요청 성공
                onSuccess.invoke()
            } else { // 요청 실패
                onFailure.invoke()
            }
        } catch(e: Exception) { // 네트워크 오류
            onErrorAction.invoke()
        }
    }
    // 이메일 중복 확인 요청 함수 (SignUpFragment1)
    fun checkDuplicateEmail(email : String, onSuccess: () -> Unit, onFailure: () -> Unit, onErrorAction: () -> Unit) = viewModelScope.launch {
        try {
            val response = api.checkDuplicateEmail(email)
            if (response.isSuccessful) { // 요청 성공
                onSuccess.invoke()
            } else { // 요청 실패
                onFailure.invoke()
            }
        } catch(e: Exception) { // 네트워크 오류
            onErrorAction.invoke()
        }
    }
    // 회원 가입 요청 함수 (SignUpFragment2)
    fun requestRegister(signUpData : SignUpData, onSuccess: () -> Unit, onFailure: () -> Unit, onErrorAction: () -> Unit) = viewModelScope.launch {
        try {
            val response = api.requestRegister(signUpData)
            if (response.isSuccessful) { // 요청 성공
                onSuccess.invoke()
            } else { // 요청 실패
                onFailure.invoke()
            }
        } catch(e: Exception) { // 네트워크 오류
            onErrorAction.invoke()
        }
    }
}