package com.project.heyboardgame.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.heyboardgame.dataModel.FcmToken
import com.project.heyboardgame.dataModel.GoogleLoginData
import com.project.heyboardgame.dataModel.GoogleRegisterData
import com.project.heyboardgame.dataStore.MyDataStore
import com.project.heyboardgame.retrofit.Api
import com.project.heyboardgame.retrofit.RetrofitClient
import com.project.heyboardgame.dataModel.LoginData
import com.project.heyboardgame.dataModel.SignUpData
import com.project.heyboardgame.dataModel.TempPasswordData
import kotlinx.coroutines.launch
import timber.log.Timber

class AuthViewModel : ViewModel() {
    // accessToken LiveData
    private val _token = MutableLiveData<String>()
    val token : LiveData<String>
        get() = _token
    // firstRun LiveData
    private val _firstRun = MutableLiveData<Boolean>()
    val firstRun : LiveData<Boolean>
        get() = _firstRun
    // DataStore
    private val myDataStore : MyDataStore = MyDataStore()
    // Api
    private val api : Api = RetrofitClient.getInstanceWithTokenInterceptor(myDataStore).create(Api::class.java)


    // accessToken가 있는 지 확인하는 함수
    fun checkAccessToken() = viewModelScope.launch {
        val getToken = myDataStore.getAccessToken()
        _token.value = getToken
    }
    // 앱 첫 실행 여부 확인하는 함수
    fun checkFirstRun() = viewModelScope.launch {
        val getFirstRun = myDataStore.getFirstRun()
        _firstRun.value = getFirstRun
    }
    // 앱 첫 실행 후 isFirstRun을 false로 변경하는 함수
    fun setIsFirstRun() = viewModelScope.launch {
        myDataStore.setFirstRun(false)
    }
    // 알림 권한 여부 dataStore에 저장하는 함수
    fun setIsNotificationAllowed(isNotificationAllowed: Boolean) = viewModelScope.launch {
        myDataStore.setNotificationAllowed(isNotificationAllowed)
    }
    // 로그인 요청 함수 (LoginFragment)
    fun requestLogin(loginData: LoginData, onSuccess: () -> Unit, onFailure: () -> Unit, onErrorAction: () -> Unit) = viewModelScope.launch {
        try {
            val response = api.requestLogin(loginData)
            if (response.isSuccessful) { // 요청 성공
                val loginResult = response.body()
                loginResult?.let {
                    // LoginResult에서 accessToken과 refreshToken 가져오기
                    val accessToken = it.result.accessToken
                    val refreshToken = it.result.refreshToken
                    val fcmToken = FcmToken(myDataStore.getFcmToken())

                    // DataStore에 저장
                    myDataStore.setAccessToken(accessToken)
                    myDataStore.setRefreshToken(refreshToken)
                    myDataStore.setGoogleLogined(false)
                    sendFcmToken(fcmToken)

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
    fun requestTempPassword(tempPasswordData: TempPasswordData, onSuccess: () -> Unit, onFailure: () -> Unit, onErrorAction: () -> Unit) = viewModelScope.launch {
        try {
            val response = api.requestTempPassword(tempPasswordData)
            if (response.isSuccessful) {
                onSuccess.invoke()
            } else {
                onFailure.invoke()
            }
        } catch(e: Exception) {
            onErrorAction.invoke()
        }
    }
    // 이메일 중복 확인 요청 함수 (SignUpFragment1)
    fun checkDuplicateEmail(email: String, onSuccess: () -> Unit, onFailure: () -> Unit, onErrorAction: () -> Unit) = viewModelScope.launch {
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
    fun requestRegister(signUpData: SignUpData, onSuccess: () -> Unit, onFailure: () -> Unit, onErrorAction: () -> Unit) = viewModelScope.launch {
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
    // 구글 로그인 요청 함수 (LoginFragment)
    fun requestGoogleLogin(googleLoginData: GoogleLoginData, onSuccess: () -> Unit, onFailure: (errorCode: Int) -> Unit, onErrorAction: () -> Unit) = viewModelScope.launch {
        try {
            val response = api.requestGoogleLogin(googleLoginData)
            if (response.isSuccessful) {
                val googleLoginResult = response.body()
                googleLoginResult?.let {
                    // LoginResult에서 accessToken과 refreshToken 가져오기
                    val accessToken = it.result.accessToken
                    val refreshToken = it.result.refreshToken

                    // DataStore에 저장
                    myDataStore.setAccessToken(accessToken)
                    myDataStore.setRefreshToken(refreshToken)
                    myDataStore.setGoogleLogined(true)

                    // MainActivity로 이동 + Toast 메세지
                    onSuccess.invoke()
                }
            } else {
                onFailure.invoke(response.code())
            }
        } catch(e: Exception) {
            onErrorAction.invoke()
        }
    }
    // 구글 회원가입 요청 함수 (GoogleSignUpFragment2)
    fun requestGoogleRegister(googleRegisterData: GoogleRegisterData, onSuccess: () -> Unit, onFailure: () -> Unit, onErrorAction: () -> Unit) = viewModelScope.launch {
        try {
            val response = api.requestGoogleRegister(googleRegisterData)
            if (response.isSuccessful) {
                val googleLoginResult = response.body()
                googleLoginResult?.let {
                    // LoginResult에서 accessToken과 refreshToken 가져오기
                    val accessToken = it.result.accessToken
                    val refreshToken = it.result.refreshToken

                    // DataStore에 저장
                    myDataStore.setAccessToken(accessToken)
                    myDataStore.setRefreshToken(refreshToken)
                    myDataStore.setGoogleLogined(true)

                    // MainActivity로 이동 + Toast 메세지
                    onSuccess.invoke()
                }
            } else {
                onFailure.invoke()
            }
        } catch(e: Exception) {
            onErrorAction.invoke()
        }
    }
    // 닉네임 중복 확인 요청 함수 (SignUpFragment1, GoogleSignUpFragment1)
    fun checkDuplicateNickname(nickname: String, onSuccess: () -> Unit, onFailure: () -> Unit, onErrorAction: () -> Unit) = viewModelScope.launch {
        try {
            val response = api.checkDuplicateNickname(nickname)
            if (response.isSuccessful) {
                onSuccess.invoke()
            } else {
                onFailure.invoke()
            }
        } catch(e: Exception) {
            onErrorAction.invoke()
        }
    }
    // FCM 토큰 전달하는 함수
    private fun sendFcmToken(fcmToken: FcmToken) = viewModelScope.launch {
        try {
            val response = api.sendFcmToken(fcmToken)
            if (response.isSuccessful) {
                Timber.d("FCM 토큰 전달 성공")
            } else {
                Timber.d("FCM 토큰 전달 실패")
            }
        } catch(e: Exception) {
            Timber.d("FCM 토큰 전달 네트워크 오류")
        }
    }
}