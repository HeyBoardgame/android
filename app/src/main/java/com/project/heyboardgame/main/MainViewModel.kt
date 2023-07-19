package com.project.heyboardgame.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.heyboardgame.dataModel.ChangePasswordData
import com.project.heyboardgame.dataStore.MyDataStore
import com.project.heyboardgame.retrofit.Api
import com.project.heyboardgame.retrofit.RetrofitClient
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    // accessToken LiveData
    private val _token = MutableLiveData<String>()
    val token : LiveData<String>
        get() = _token
    // DataStore
    private val myDataStore : MyDataStore = MyDataStore()
    // Api
    private val api : Api = RetrofitClient.getInstance(myDataStore).create(Api::class.java)

    // 로그아웃 함수
    fun requestLogout(onSuccess: () -> Unit, onFailure: () -> Unit, onErrorAction: () -> Unit) = viewModelScope.launch {
        try {
            val response = api.requestLogout()
            if (response.isSuccessful) {
                myDataStore.setAccessToken("")
                myDataStore.setRefreshToken("")
                onSuccess.invoke()
            } else {
                onFailure.invoke()
            }
        } catch(e: Exception) {
            onErrorAction.invoke()
        }
    }
    // 회원 탈퇴 함수
    fun requestUnregister(onSuccess: () -> Unit, onFailure: () -> Unit, onErrorAction: () -> Unit) = viewModelScope.launch {
        try {
            val response = api.requestUnregister()
            if (response.isSuccessful) {
                myDataStore.setAccessToken("")
                myDataStore.setRefreshToken("")
                onSuccess.invoke()
            } else {
                onFailure.invoke()
            }
        } catch(e: Exception) {
            onErrorAction.invoke()
        }
    }
    // 비밀번호 변경 함수
    fun requestNewPassword(changePasswordData: ChangePasswordData, onSuccess: () -> Unit, onFailure: () -> Unit, onErrorAction: () -> Unit) = viewModelScope.launch {
        try {
            val response = api.requestNewPassword(changePasswordData)
            if (response.isSuccessful) {
                onSuccess.invoke()
            } else {
                onFailure.invoke()
            }
        } catch(e: Exception) {
            onErrorAction.invoke()
        }
    }

}