package com.project.heyboardgame.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.heyboardgame.dataStore.MyDataStore
import kotlinx.coroutines.launch
import timber.log.Timber

class AuthViewModel : ViewModel() {

    private val _logined = MutableLiveData<Boolean>()
    val logined : LiveData<Boolean>
        get() = _logined

    fun checkLoginFlag() = viewModelScope.launch {

        val getData = MyDataStore().getLoginData()

        _logined.value = getData

        Timber.d(getData.toString())

    }
}