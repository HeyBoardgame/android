package com.project.heyboardgame.main


import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.heyboardgame.App
import com.project.heyboardgame.R
import com.project.heyboardgame.dataModel.ChangePasswordData
import com.project.heyboardgame.dataModel.ChangeProfileData
import com.project.heyboardgame.dataStore.MyDataStore
import com.project.heyboardgame.retrofit.Api
import com.project.heyboardgame.retrofit.RetrofitClient
import kotlinx.coroutines.launch
import timber.log.Timber

class MainViewModel : ViewModel() {
    // DataStore
    private val myDataStore : MyDataStore = MyDataStore()
    // Api
    private val api : Api = RetrofitClient.getInstance(myDataStore).create(Api::class.java)

    // 로그아웃 함수 (ProfileFragment)
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
    // 회원 탈퇴 함수 (UnregisterFragment)
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
    // 비밀번호 변경 함수 (ChangePwdFragment)
    fun changePassword(changePasswordData: ChangePasswordData, onSuccess: () -> Unit, onFailure: () -> Unit, onErrorAction: () -> Unit) = viewModelScope.launch {
        try {
            val response = api.changePassword(changePasswordData)
            if (response.isSuccessful) {
                onSuccess.invoke()
            } else {
                onFailure.invoke()
            }
        } catch(e: Exception) {
            onErrorAction.invoke()
        }
    }
    // 프로필 정보 요청 함수 (MainActivity)
    fun requestMyProfile(onErrorAction: () -> Unit) = viewModelScope.launch {
        try {
            val response = api.requestMyProfile()
            if (response.isSuccessful) {
                val profileResult = response.body() // 서버에서 받아온 데이터
                profileResult?.let {
                    val profileImg = it.result.profileImg
                    val nickname = it.result.nickname
                    val friendCode = it.result.friendCode

                    myDataStore.setProfileImage(profileImg)
                    myDataStore.setNickname(nickname)
                    myDataStore.setFriendCode(friendCode)

                    Timber.d("프로필 조회 성공")
                }
            } else {
                //myDataStore.setProfileImage("android.resource://${App.getContext().packageName}/${R.drawable.default_profile_img}")
//                myDataStore.setProfileImage("")
//                myDataStore.setNickname("새로운닉네임")
//                myDataStore.setFriendCode("G40123")
                Timber.d("프로필 조회 실패")
            }
        } catch (e: Exception) {
            onErrorAction.invoke()
        }
    }

    fun changeMyProfile(changeProfileData: ChangeProfileData, onSuccess: () -> Unit, onFailure: () -> Unit, onErrorAction: () -> Unit) = viewModelScope.launch {
        try {
            val response = api.changeMyProfile(changeProfileData)
            if (response.isSuccessful) {
                if (changeProfileData.isChanged) {
                    myDataStore.setProfileImage(changeProfileData.profileImg)
                }
                myDataStore.setNickname(changeProfileData.nickname)
                onSuccess.invoke()
            } else {
                onFailure.invoke()
            }
        } catch(e: Exception) {
            onErrorAction.invoke()
        }
    }

}