package com.project.heyboardgame.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.heyboardgame.dataStore.MyDataStore
import kotlinx.coroutines.launch
import timber.log.Timber


class AuthViewModel : ViewModel() {

    private val _token = MutableLiveData<String>()
    val token : LiveData<String>
        get() = _token

    fun checkAccessToken() = viewModelScope.launch {

        val getToken = MyDataStore().getAccessToken()
        _token.value = getToken

        Timber.d(getToken)
    }

    fun setAccessToken(accessToken : String) = viewModelScope.launch {
        MyDataStore().setAccessToken(accessToken)
    }

}