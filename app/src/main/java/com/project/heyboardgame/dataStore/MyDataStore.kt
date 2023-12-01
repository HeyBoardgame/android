package com.project.heyboardgame.dataStore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.project.heyboardgame.App
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class MyDataStore {

    private val context = App.getContext()

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("user_pref")
    }

    private val mDataStore: DataStore<Preferences> = context.dataStore
    // 토큰
    private val ACCESS_TOKEN = stringPreferencesKey("ACCESS_TOKEN")
    private val REFRESH_TOKEN = stringPreferencesKey("REFRESH_TOKEN")
    // 구글 로그인 여부
    private val GOOGLE_LOGINED = booleanPreferencesKey("GOOGLE_LOGINED")
    // 앱 첫 실행 여부
    private val IS_FIRST_RUN = booleanPreferencesKey("IS_FIRST_RUN")
    // 알림 권한 허용 여부
    private val NOTIFICATION_ALLOWED = booleanPreferencesKey("NOTIFICATION_ALLOWED")
    // FCM 토큰
    private val FCM_TOKEN = stringPreferencesKey("FCM_TOKEN")

    suspend fun setAccessToken(accessToken: String) {
        mDataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = accessToken
        }
    }

    suspend fun setRefreshToken(refreshToken: String) {
        mDataStore.edit { preferences ->
            preferences[REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun setGoogleLogined(googleLogined: Boolean) {
        mDataStore.edit { preferences ->
            preferences[GOOGLE_LOGINED] = googleLogined
        }
    }

    suspend fun setFirstRun(isFirstRun: Boolean) {
        mDataStore.edit { preferences ->
            preferences[IS_FIRST_RUN] = isFirstRun
        }
    }

    suspend fun setNotificationAllowed(isNotificationAllowed: Boolean) {
        mDataStore.edit { preferences ->
            preferences[NOTIFICATION_ALLOWED] = isNotificationAllowed
        }
    }

    suspend fun setFcmToken(fcmToken: String) {
        mDataStore.edit { preferences ->
            preferences[FCM_TOKEN] = fcmToken
        }
    }

    suspend fun getFcmToken(): String {
        var currentValue = ""
        mDataStore.edit { preferences ->
            currentValue = if (preferences[FCM_TOKEN] != null) {
                preferences[FCM_TOKEN].toString()
            } else {
                ""
            }
        }
        return currentValue
    }

    suspend fun getAccessToken(): String {
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

    suspend fun getRefreshToken(): String {
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

    suspend fun getGoogleLogined(): Boolean {
        var currentValue = false
        mDataStore.edit { preferences ->
            currentValue = preferences[GOOGLE_LOGINED] ?: false
        }
        return currentValue
    }

    suspend fun getFirstRun(): Boolean {
        return mDataStore.data.map { preferences ->
            preferences[IS_FIRST_RUN] ?: true
        }.first()
    }

    suspend fun getNotificationAllowed(): Boolean {
        var currentValue = false
        mDataStore.edit { preferences ->
            currentValue = preferences[NOTIFICATION_ALLOWED] ?: false
        }
        return currentValue
    }
}