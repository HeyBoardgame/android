package com.project.heyboardgame.main


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.gson.Gson
import com.project.heyboardgame.dataModel.BoardGame
import com.project.heyboardgame.dataModel.BoardGame2
import com.project.heyboardgame.dataModel.ChangePasswordData
import com.project.heyboardgame.dataModel.ChangeProfileData
import com.project.heyboardgame.dataModel.DetailResultData
import com.project.heyboardgame.dataModel.Friend
import com.project.heyboardgame.dataModel.FriendRequestData
import com.project.heyboardgame.dataModel.RatedResultData
import com.project.heyboardgame.dataModel.RatingData
import com.project.heyboardgame.dataModel.SearchResultData
import com.project.heyboardgame.dataStore.MyDataStore
import com.project.heyboardgame.paging.BookmarkPagingSource
import com.project.heyboardgame.paging.FriendListPagingSource
import com.project.heyboardgame.paging.FriendRequestPagingSource
import com.project.heyboardgame.paging.RatedPagingSource
import com.project.heyboardgame.paging.SearchPagingSource
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
    // 찜한 목록 LiveData
    private val _bookmarkPagingData = MutableLiveData<Flow<PagingData<BoardGame>>>()
    val bookmarkPagingData: LiveData<Flow<PagingData<BoardGame>>> = _bookmarkPagingData
    // 평가한 목록 LiveData
    private val _ratedPagingData = MutableLiveData<Flow<PagingData<BoardGame>>>()
    val ratedPagingData: LiveData<Flow<PagingData<BoardGame>>> = _ratedPagingData
    // 검색 결과 LiveData
    private val _searchPagingData = MutableLiveData<Flow<PagingData<BoardGame2>>>()
    val searchPagingData: LiveData<Flow<PagingData<BoardGame2>>> = _searchPagingData
    // 친구 목록 LiveData
    private val _friendListPagingData = MutableLiveData<Flow<PagingData<Friend>>>()
    val friendListPagingData: LiveData<Flow<PagingData<Friend>>> = _friendListPagingData
    // 친구 요청 목록 LiveData
    private val _friendRequestPagingData = MutableLiveData<Flow<PagingData<Friend>>>()
    val friendRequestPagingData: LiveData<Flow<PagingData<Friend>>> = _friendRequestPagingData
    // 검색 결과 유지
    private var currentSearchKeyword = ""
    private var currentGenreIdList = emptyList<Int>()
    private var currentNumOfPlayer = 0


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
    fun getMyProfile(onErrorAction: () -> Unit) = viewModelScope.launch {
        try {
            val response = api.getMyProfile()
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
    // 프로필 수정 함수 (ChangeProfileFragment)
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
    // 보드게임 상세 조회 (DetailFragment)
    fun getDetail(id: Int, onSuccess: (detailResult: DetailResultData) -> Unit, onFailure: () -> Unit, onErrorAction: () -> Unit) = viewModelScope.launch {
        try {
            val response = api.getDetail(id)
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
    // 보드게임 평가하기 (DetailFragment)
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

    // 보드게임 찜하기 (DetailFragment)
    fun addBookmark(id: Int, onSuccess: () -> Unit, onFailure: () -> Unit, onErrorAction: () -> Unit) = viewModelScope.launch {
        try {
            val response = api.addBookmark(id)
            if(response.isSuccessful) {
                onSuccess.invoke()
            } else {
                onFailure.invoke()
            }
        } catch(e: Exception) {
            onErrorAction.invoke()
        }
    }
    // 보드게임 찜하기 취소 (DetailFragment)
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
    // 찜한 목록 페이징 (BookmarkFragment)
    fun loadBookmarkPagingData(sort: String) {
        val size = 20
        val pagingDataFlow = Pager(
            config = PagingConfig(pageSize = size, initialLoadSize = 10),
            pagingSourceFactory = { BookmarkPagingSource(api, sort, size) }
        ).flow

        _bookmarkPagingData.value = pagingDataFlow
    }
    // 특정 평점 평가한 목록 페이징 (SpecificRateFragment)
    fun loadRatedPagingData(score: Float, sort: String) {
        val size = 20
        val pagingDataFlow = Pager(
            config = PagingConfig(pageSize = size, initialLoadSize = 10),
            pagingSourceFactory = { RatedPagingSource(api, score, sort, size) }
        ).flow

        _ratedPagingData.value = pagingDataFlow
    }
    // 평가한 보드게임 목록 조회 (RatedFragment)
    fun getRatedList(onSuccess: (ratedResultData: RatedResultData) -> Unit, onFailure: () -> Unit, onErrorAction: () -> Unit) = viewModelScope.launch {
        try {
            val response = api.getRatedList()
            if (response.isSuccessful) {
                val ratedResult = response.body()
                ratedResult?.let {
                    onSuccess(it.result)
                }
            } else {
                onFailure.invoke()
            }
        } catch(e: Exception) {
            onErrorAction.invoke()
        }
    }
    // 검색 결과 페이징 (SearchFragment)
    fun loadSearchPagingData(keyword: String, genreIdList: List<Int>, numOfPlayer: Int) {
        setCurrentSearchQuery(keyword, genreIdList, numOfPlayer)
        val size = 20
        val pagingDataFlow = Pager(
            config = PagingConfig(pageSize = size, initialLoadSize = 10),
            pagingSourceFactory = { SearchPagingSource(api, keyword, genreIdList, numOfPlayer, size) }
        ).flow

        _searchPagingData.value = pagingDataFlow
    }
    private fun setCurrentSearchQuery(keyword: String, genreIdList: List<Int>, numOfPlayer: Int) {
        currentSearchKeyword = keyword
        currentGenreIdList = genreIdList
        currentNumOfPlayer = numOfPlayer
    }
    fun getCurrentSearchQuery(): Triple<String, List<Int>, Int> {
        return Triple(currentSearchKeyword, currentGenreIdList, currentNumOfPlayer)
    }
    // 친구 요청 보내기 (AddFriendFragment)
    fun sendFriendRequest(friendRequestData: FriendRequestData, onSuccess: () -> Unit, onFailure: () -> Unit, onErrorAction: () -> Unit) = viewModelScope.launch {
        try {
            val response = api.sendFriendRequest(friendRequestData)
            if (response.isSuccessful) {
                onSuccess.invoke()
            } else {
                onFailure.invoke()
            }
        } catch(e: Exception) {
            onErrorAction.invoke()
        }
    }
    // 친구 목록 페이징 (SocialFragment)
    fun loadFriendListPagingData() {
        val size = 15
        val pagingDataFlow = Pager(
            config = PagingConfig(pageSize = size),
            pagingSourceFactory = { FriendListPagingSource(api, size) }
        ).flow

        _friendListPagingData.value = pagingDataFlow
    }
    // 친구 요청 목록 페이징 (FriendRequestFragment)
    fun loadFriendRequestPagingData() {
        val size = 15
        val pagingDataFlow = Pager(
            config = PagingConfig(pageSize = size),
            pagingSourceFactory = { FriendRequestPagingSource(api, size) }
        ).flow

        _friendRequestPagingData.value = pagingDataFlow
    }
    // 상위 n개 친구 요청 목록 조회
    fun getFriendRequestList(onSuccess: (friends: List<Friend>) -> Unit, onFailure: () -> Unit, onErrorAction: () -> Unit) = viewModelScope.launch {
        try {
            val response = api.getFriendRequestList()
            if (response.isSuccessful) {
                val friendResult = response.body()
                if (friendResult != null) {
                    onSuccess.invoke(friendResult.result.friends)
                } else {
                    onFailure.invoke()
                }
            }
        } catch(e: Exception) {
            onErrorAction.invoke()
        }
    }
    // 친구 요청 수락
    fun acceptFriendRequest(id: Int, onSuccess: () -> Unit, onFailure: () -> Unit, onErrorAction: () -> Unit) = viewModelScope.launch {
        try {
            val response = api.acceptFriendRequest(id)
            if (response.isSuccessful) {
                onSuccess.invoke()
            } else {
                onFailure.invoke()
            }
        } catch(e: Exception) {
            onErrorAction.invoke()
        }
    }
    // 친구 요청 거절
    fun declineFriendRequest(id: Int, onSuccess: () -> Unit, onFailure: () -> Unit, onErrorAction: () -> Unit) = viewModelScope.launch {
        try {
            val response = api.declineFriendRequest(id)
            if (response.isSuccessful) {
                onSuccess.invoke()
            } else {
                onFailure.invoke()
            }
        } catch(e: Exception) {
            onErrorAction.invoke()
        }
    }
}