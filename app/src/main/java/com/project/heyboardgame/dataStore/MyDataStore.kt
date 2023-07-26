package com.project.heyboardgame.dataStore

import android.content.Context
import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.project.heyboardgame.App

class MyDataStore {

    private val context = App.getContext()

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("user_pref")
    }

    private val mDataStore: DataStore<Preferences> = context.dataStore
    // 토큰
    private val ACCESS_TOKEN = stringPreferencesKey("ACCESS_TOKEN")
    private val REFRESH_TOKEN = stringPreferencesKey("REFRESH_TOKEN")
    // 프로필 정보
    private val PROFILE_IMAGE_URI = stringPreferencesKey("PROFILE_IMAGE_URI")
    private val NICKNAME = stringPreferencesKey("NICKNAME")
    private val USER_CODE = stringPreferencesKey("USER_CODE")


    // 로그인 성공 시 액세스 토큰 저장
    suspend fun setAccessToken(accessToken: String) {
        mDataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = accessToken
        }
    }

    // 로그인 성공 시 리프레시 토큰 저장
    suspend fun setRefreshToken(refreshToken: String) {
        mDataStore.edit { preferences ->
            preferences[REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun setProfileImage(profileImg: String) {
        mDataStore.edit { preferences ->
            preferences[PROFILE_IMAGE_URI] = profileImg
        }
    }

    suspend fun setNickname(nickname : String) {
        mDataStore.edit { preferences ->
            preferences[NICKNAME] = nickname
        }
    }

    suspend fun setUserCode(userCode : String) {
        mDataStore.edit { preferences ->
            preferences[USER_CODE] = userCode
        }
    }

    // 액세스 토큰이 있으면 액세스 토큰 반환, 없으면 빈 문자열 반환
    suspend fun getAccessToken() : String {
        var currentValue = ""
        mDataStore.edit { preferences ->
            currentValue = if (preferences[ACCESS_TOKEN] != null) {
                preferences[ACCESS_TOKEN].toString()
            } else {
                ""
            }
        }
        return currentValue
    }

    // 리프레시 토큰 반환
    suspend fun getRefreshToken() : String {
        var currentValue = ""
        mDataStore.edit { preferences ->
            currentValue = if (preferences[REFRESH_TOKEN] != null) {
                preferences[REFRESH_TOKEN].toString()
            } else {
                ""
            }
        }
        return currentValue
    }

    // 프로필 이미지 URI 불러오기
    suspend fun getProfileImage(): Uri? {
        var currentValue: String? = ""
        mDataStore.edit { preferences ->
            currentValue = preferences[PROFILE_IMAGE_URI]
        }
        return if (currentValue.isNullOrEmpty()) null else Uri.parse(currentValue)
    }

    suspend fun getNickname() : String {
        var currentValue = ""
        mDataStore.edit { preferences ->
            currentValue = if (preferences[NICKNAME] != null) {
                preferences[NICKNAME].toString()
            } else {
                ""
            }
        }
        return currentValue
    }

    suspend fun getUserCode() : String {
        var currentValue = ""
        mDataStore.edit { preferences ->
            currentValue = if (preferences[USER_CODE] != null) {
                preferences[USER_CODE].toString()
            } else {
                ""
            }
        }
        return currentValue
    }
}