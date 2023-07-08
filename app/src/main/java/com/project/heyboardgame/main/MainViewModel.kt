package com.project.heyboardgame.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.heyboardgame.dataStore.MyDataStore
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    fun setUpLoginFlag() = viewModelScope.launch {
        MyDataStore().setUpLoginData()
    }

}