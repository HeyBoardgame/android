package com.project.heyboardgame.main


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.Gson
import com.project.heyboardgame.dataModel.BoardGame
import com.project.heyboardgame.dataModel.ChangePasswordData
import com.project.heyboardgame.dataModel.ChangeProfileData
import com.project.heyboardgame.dataModel.DetailResultData
import com.project.heyboardgame.dataModel.HistoryResultData
import com.project.heyboardgame.dataModel.RatingData
import com.project.heyboardgame.dataModel.SearchResultData
import com.project.heyboardgame.dataStore.MyDataStore
import com.project.heyboardgame.paging.BookmarkPagingSource
import com.project.heyboardgame.retrofit.Api
import com.project.heyboardgame.retrofit.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import timber.log.Timber

class MainViewModel : ViewModel() {
    // DataStore
    private val myDataStore : MyDataStore = MyDataStore()
    // Api
    private val api : Api = RetrofitClient.getInstanceWithTokenInterceptor(myDataStore).create(Api::class.java)
    private val _bookmarkPagingData = MutableLiveData<Flow<PagingData<BoardGame>>>()
    val bookmarkPagingData: LiveData<Flow<PagingData<BoardGame>>> = _bookmarkPagingData

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
                    val userCode = it.result.userCode

                    myDataStore.setProfileImage(profileImg)
                    myDataStore.setNickname(nickname)
                    myDataStore.setUserCode(userCode)

                    Timber.d("프로필 조회 성공")
                }
            } else {
                Timber.d("프로필 조회 실패")
            }
        } catch (e: Exception) {
            onErrorAction.invoke()
        }
    }
    // 프로필 수정 함수
    fun changeMyProfile(profileImg: String, file: MultipartBody.Part?, changeProfileData: ChangeProfileData, onSuccess: () -> Unit, onFailure: () -> Unit, onErrorAction: () -> Unit) = viewModelScope.launch {
        val json = Gson().toJson(changeProfileData) // ChangeProfileData를 JSON으로 직렬화
        // ChangeProfileData를 RequestBody로 변환
        val requestBody = json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val response = api.changeMyProfile(file, requestBody)
        try {
            if (response.isSuccessful) {
                if (changeProfileData.isChanged) {
                    myDataStore.setProfileImage(profileImg)
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
    // 보드게임 검색 함수
    fun requestSearchResult(keyword: String, genreIdList: List<Int>, numOfPlayer: Int, onSuccess: (searchResultList: List<SearchResultData>?) -> Unit, onFailure: () -> Unit, onErrorAction: () -> Unit) = viewModelScope.launch {
        try {
            val response = api.requestSearchResult(keyword, genreIdList, numOfPlayer)
            if (response.isSuccessful) {
                val searchResultList = response.body()
                onSuccess.invoke(searchResultList?.result)
            } else {
                onFailure.invoke()
            }
        } catch(e: Exception) {
            onErrorAction.invoke()
        }
    }
    // 보드게임 상세 조회
    fun requestDetail(id: Int, onSuccess: (detailResult: DetailResultData) -> Unit, onFailure: () -> Unit, onErrorAction: () -> Unit) = viewModelScope.launch {
        try {
            val response = api.requestDetail(id)
            if (response.isSuccessful) {
                val detailResult = response.body()
                detailResult?.let {
                    onSuccess(it.result)
                }
            } else {
                onFailure.invoke()
            }
        } catch(e: Exception) {
            onErrorAction.invoke()
        }
    }
    // 보드게임 평가하기
    fun requestRating(id: Int, ratingData: RatingData, onSuccess: () -> Unit, onFailure: () -> Unit, onErrorAction: () -> Unit) = viewModelScope.launch {
        try {
            val response = api.requestRating(id, ratingData)
            if (response.isSuccessful) {
                onSuccess.invoke()
            } else {
                onFailure.invoke()
            }
        } catch(e: Exception) {
            onErrorAction.invoke()
        }
    }
    // 보드게임 찜하기
    fun requestBookmark(id: Int, onSuccess: () -> Unit, onFailure: () -> Unit, onErrorAction: () -> Unit) = viewModelScope.launch {
        try {
            val response = api.requestBookmark(id)
            if(response.isSuccessful) {
                onSuccess.invoke()
            } else {
                onFailure.invoke()
            }
        } catch(e: Exception) {
            onErrorAction.invoke()
        }
    }

    // 보드게임 찜하기 취소
    fun deleteBookmark(id: Int, onSuccess: () -> Unit, onFailure: () -> Unit, onErrorAction: () -> Unit) = viewModelScope.launch {
        try {
            val response = api.deleteBookmark(id)
            if (response.isSuccessful) {
                onSuccess.invoke()
            } else {
                onFailure.invoke()
            }
        } catch(e: Exception) {
            onErrorAction.invoke()
        }
    }

    fun loadBookmarkPagingData(sort: String) {
        val pagingDataFlow = Pager(
            config = PagingConfig(pageSize = 20, initialLoadSize = 10),
            pagingSourceFactory = { BookmarkPagingSource(api, sort) }
        ).flow

        _bookmarkPagingData.value = pagingDataFlow
    }

}