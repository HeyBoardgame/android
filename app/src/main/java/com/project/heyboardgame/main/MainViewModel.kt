package com.project.heyboardgame.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun requestLogout(onSuccess: () -> Unit, onFailure: () -> Unit, onErrorAction: () -> Unit) = viewModelScope.launch {
        try {
            val response = api.requestLogout()
            if (response.isSuccessful) { // 로그아웃 성공
                myDataStore.setAccessToken("")
                myDataStore.setRefreshToken("")
                onSuccess.invoke()
            } else { // 로그아웃 실패
                onFailure.invoke()
            }
        } catch(e: Exception) { // 네트워크 오류
            onErrorAction.invoke()
        }
    }

    fun requestUnregister(onSuccess: () -> Unit, onFailure: () -> Unit, onErrorAction: () -> Unit) = viewModelScope.launch {
        try {
            val response = api.requestUnregister()
            if (response.isSuccessful) { // 회원 탈퇴 성공
                myDataStore.setAccessToken("")
                myDataStore.setRefreshToken("")
                onSuccess.invoke()
            } else { // 회원 탈퇴 실패
                onFailure.invoke()
            }
        } catch(e: Exception) { // 네트워크 오류
            onErrorAction.invoke()
        }
    }

}