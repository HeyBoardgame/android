package com.project.heyboardgame.dataStore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.project.heyboardgame.App

class MyDataStore {

    private val context = App.context()

    companion object {
        private val Context.dataStore : DataStore<Preferences> by preferencesDataStore("user_pref")
    }

    private val mDataStore : DataStore<Preferences> = context.dataStore

    private val ACCESS_TOKEN = stringPreferencesKey("ACCESS_TOKEN")

    // 로그인 성공 시 토큰 저장
    suspend fun setAccessToken(accessToken : String) {
        mDataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = accessToken
        }
    }

    // 토큰이 있으면 토큰 반환, 없으면 empty 반환
    suspend fun getAccessToken() : String {

        var currentValue = ""

        mDataStore.edit { preferences ->
            currentValue = if (preferences[ACCESS_TOKEN] != null) {
                preferences[ACCESS_TOKEN].toString()
            } else {
                "empty"
            }
        }
        return currentValue
    }
}