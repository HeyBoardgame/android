package com.project.heyboardgame.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.heyboardgame.dataStore.MyDataStore
import com.project.heyboardgame.retrofit.Api
import com.project.heyboardgame.retrofit.RetrofitClient
import kotlinx.coroutines.launch
import timber.log.Timber

class MainViewModel : ViewModel() {
    // accessToken LiveData
    private val _token = MutableLiveData<String>()
    val token : LiveData<String>
        get() = _token
    // DataStore
    private val myDataStore : MyDataStore = MyDataStore()
    // Api
    private val api : Api = RetrofitClient.getInstance(myDataStore).create(Api::class.java)

    fun checkAccessToken() = viewModelScope.launch {

        val getToken = myDataStore.getAccessToken()
        _token.value = getToken

        Timber.d(getToken)
    }

}