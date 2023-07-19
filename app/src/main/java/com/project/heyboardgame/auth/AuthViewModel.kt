package com.project.heyboardgame.auth

import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.heyboardgame.App
import com.project.heyboardgame.dataStore.MyDataStore
import com.project.heyboardgame.main.MainActivity
import com.project.heyboardgame.retrofit.Api
import com.project.heyboardgame.retrofit.RetrofitClient
import com.project.heyboardgame.dataModel.LoginData
import com.project.heyboardgame.dataModel.LoginResult
import com.project.heyboardgame.dataModel.SignUpData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
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
    // 로그인 요청 함수
//    fun requestLogin(loginData: LoginData) {
//        val call = api.requestLogin(loginData)
//        call.enqueue(object : retrofit2.Callback<LoginResult> {
//            override fun onResponse(call: Call<LoginResult>, response: Response<LoginResult>) {
//                if (response.isSuccessful) {
//                    val loginResult = response.body()
//                    loginResult?.let {
//                        // LoginResult에서 accessToken과 refreshToken 가져오기
//                        val accessToken = it.result.accessToken
//                        val refreshToken = it.result.refreshToken
//
//                        // DataStore에 저장
//                        viewModelScope.launch {
//                            myDataStore.setAccessToken(accessToken ?: "")
//                            myDataStore.setRefreshToken(refreshToken ?: "")
//                        }
//
//                        // MainActivity로 이동하는 코드 작성
//                        val intent = Intent(App.getContext(), MainActivity::class.java)
//                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                        App.getContext().startActivity(intent)
//                        Toast.makeText(App.getContext(), "로그인 성공!", Toast.LENGTH_SHORT).show()
//                    }
//                } else { // 로그인 실패 처리
//                    val errorMessage = "아이디 또는 비밀번호를 다시 한 번 확인해주세요."
//                    Toast.makeText(App.getContext(), errorMessage, Toast.LENGTH_SHORT).show()
//                }
//            }
//            override fun onFailure(call: Call<LoginResult>, t: Throwable) { // 네트워크 오류 처리
//                val errorMessage = "네트워크 오류가 발생했습니다."
//                Toast.makeText(App.getContext(), errorMessage, Toast.LENGTH_SHORT).show()
//            }
//        })
//    }
    fun requestLogin(loginData: LoginData) = viewModelScope.launch {
        try {
            val response: Response<LoginResult> = api.requestLogin(loginData)
            if (response.isSuccessful) { // 로그인 성공
                val loginResult = response.body()
                loginResult?.let {
                    // LoginResult에서 accessToken과 refreshToken 가져오기
                    val accessToken = it.result.accessToken
                    val refreshToken = it.result.refreshToken

                    // DataStore에 저장
                    myDataStore.setAccessToken(accessToken ?: "")
                    myDataStore.setRefreshToken(refreshToken ?: "")

                    // MainActivity로 이동하는 코드 작성
                    val intent = Intent(App.getContext(), MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    App.getContext().startActivity(intent)

                    withContext(Dispatchers.Main) {
                        Toast.makeText(App.getContext(), "로그인 성공!", Toast.LENGTH_SHORT).show()
                    }
                }
            } else { // 서버로부터 로그인 실패 응답을 받았을 때 처리하는 로직
                withContext(Dispatchers.Main) {
                    val errorMessage = "아이디 또는 비밀번호를 다시 한 번 확인해주세요."
                    Toast.makeText(App.getContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) { // 네트워크 오류 등으로 요청이 실패했을 때 처리하는 로직
            withContext(Dispatchers.Main) {
                val errorMessage = "네트워크 오류가 발생했습니다."
                Toast.makeText(App.getContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }
    // 임시 비밀번호 발급 요청 함수
//    fun requestNewPassword(email: String, onSuccess: () -> Unit, onFailure: () -> Unit, onErrorAction: () -> Unit) {
//        val call = api.requestNewPassword(email)
//        call.enqueue(object : Callback<Void> {
//            override fun onResponse(call: Call<Void>, response: Response<Void>) {
//                if (response.isSuccessful) {
//                    onSuccess.invoke()
//                } else {
//                    onFailure.invoke()
//                }
//            }
//            override fun onFailure(call: Call<Void>, t: Throwable) { // 네트워크 오류 처리
//                onErrorAction.invoke()
//                val errorMessage = "네트워크 오류가 발생했습니다."
//                Toast.makeText(App.getContext(), errorMessage, Toast.LENGTH_SHORT).show()
//            }
//        })
//    }
    fun requestNewPassword(email: String, onSuccess: () -> Unit, onFailure: () -> Unit, onErrorAction: () -> Unit) = viewModelScope.launch {
        try {
            val response = api.requestNewPassword(email)
            if (response.isSuccessful) {
                onSuccess.invoke()
            } else {
                onFailure.invoke()
            }
        } catch(e: Exception) {
            onErrorAction.invoke()
            withContext(Dispatchers.Main) {
                val errorMessage = "네트워크 오류가 발생했습니다."
                Toast.makeText(App.getContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }
    // 이메일 중복 확인 요청 함수
//    fun checkDuplicateEmail(email : String, onSuccess: () -> Unit, onFailure: () -> Unit) {
//        val call = api.checkDuplicateEmail(email)
//        call.enqueue(object : Callback<Void> {
//            override fun onResponse(call: Call<Void>, response: Response<Void>) {
//                if (response.isSuccessful) { // 이메일 중복이 아닌 경우
//                    onSuccess.invoke()
//                } else { // 이메일 중복인 경우
//                    onFailure.invoke()
//                }
//            }
//            override fun onFailure(call: Call<Void>, t: Throwable) { // 네트워크 오류 처리
//                val errorMessage = "네트워크 오류가 발생했습니다."
//                Toast.makeText(App.getContext(), errorMessage, Toast.LENGTH_SHORT).show()
//            }
//        })
//    }
    fun checkDuplicateEmail(email : String, onSuccess: () -> Unit, onFailure: () -> Unit) = viewModelScope.launch {
        try {
            val response = api.checkDuplicateEmail(email)
            if (response.isSuccessful) {
                onSuccess.invoke()
            } else {
                onFailure.invoke()
            }
        } catch(e: Exception) {
            withContext(Dispatchers.Main) {
                val errorMessage = "네트워크 오류가 발생했습니다."
                Toast.makeText(App.getContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }
    // 회원 가입 요청 함수
//    fun requestRegister(signUpData : SignUpData, onSuccess: () -> Unit, onFailure: () -> Unit) {
//        val call = api.requestRegister(signUpData)
//        call.enqueue(object : retrofit2.Callback<Void> {
//            override fun onResponse(call: Call<Void>, response: Response<Void>) {
//                if (response.isSuccessful) { // 회원 가입 성공
//                    onSuccess.invoke()
//                } else { // 회원 가입 실패
//                    onFailure.invoke()
//                }
//            }
//            override fun onFailure(call: Call<Void>, t: Throwable) { // 네트워크 오류 처리
//                val errorMessage = "네트워크 오류가 발생했습니다."
//                Toast.makeText(App.getContext(), errorMessage, Toast.LENGTH_SHORT).show()
//            }
//        })
//    }
    fun requestRegister(signUpData : SignUpData, onSuccess: () -> Unit, onFailure: () -> Unit) = viewModelScope.launch {
        try {
            val response = api.requestRegister(signUpData)
            if (response.isSuccessful) {
                onSuccess.invoke()
            } else {
                onFailure.invoke()
            }
        } catch(e: Exception) {
            withContext(Dispatchers.Main) {
                val errorMessage = "네트워크 오류가 발생했습니다."
                Toast.makeText(App.getContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }
}