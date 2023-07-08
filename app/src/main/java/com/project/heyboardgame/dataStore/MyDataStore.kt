package com.project.heyboardgame.dataStore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.project.heyboardgame.App


class MyDataStore {

    private val context = App.context()

    companion object {
        // Context를 상속하는 클래스인 Activity나 Fragment에서 직접 접근이 가능하며, Context가 있는 곳 어디에서든지 사용 가능
        private val Context.dataStore : DataStore<Preferences> by preferencesDataStore("user_pref")
    }

    private val mDataStore : DataStore<Preferences> = context.dataStore
    private val LOGIN_FLAG = booleanPreferencesKey("LOGIN_FLAG") // login 여부를 알려주는 변수

    // 로그인해서 MainActivity로 갈 때 TRUE로 변경하는 함수
    // 로그인 한 유저이면 TRUE, 안 한 유저이면 FALSE
    suspend fun setUpLoginData() {
        mDataStore.edit { preferences ->
            preferences[LOGIN_FLAG] = true
        }
    }

    // LOGIN_FLAG 값을 가져오는 함수
    suspend fun getLoginData() : Boolean {

        var currentValue = false

        mDataStore.edit { preferences ->
            currentValue = preferences[LOGIN_FLAG] ?: false
        }

        return currentValue

    }
}