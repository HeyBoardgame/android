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
import com.project.heyboardgame.dataModel.Chat
import com.project.heyboardgame.dataModel.ChatResultData
import com.project.heyboardgame.dataModel.ChatRoom
import com.project.heyboardgame.dataModel.DetailResultData
import com.project.heyboardgame.dataModel.Friend
import com.project.heyboardgame.dataModel.FriendRequestData
import com.project.heyboardgame.dataModel.GroupMatchData
import com.project.heyboardgame.dataModel.GroupMatchResultData
import com.project.heyboardgame.dataModel.GroupRecommendData
import com.project.heyboardgame.dataModel.GroupRecommendResultData
import com.project.heyboardgame.dataModel.MyProfileResultData
import com.project.heyboardgame.dataModel.PersonalRecResultData
import com.project.heyboardgame.dataModel.RatedResultData
import com.project.heyboardgame.dataModel.RatingData
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
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

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
    // 검색 결과 변수
    private var currentSearchKeyword = ""
    private var currentGenreIdList = emptyList<Int>()
    private var currentNumOfPlayer = 0
    // 채팅방 대화 LiveData
    private val _chatPagingData = MutableLiveData<Flow<PagingData<Chat>>>()
    val chatPagingData: LiveData<Flow<PagingData<Chat>>> = _chatPagingData
    // googleLogined LiveData
    private val _googleLogined = MutableLiveData<Boolean>()
    val googleLogined : LiveData<Boolean>
        get() = _googleLogined


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
    fun getMyProfile(onSuccess: (myProfileResultData: MyProfileResultData) -> Unit, onFailure: () -> Unit, onErrorAction: () -> Unit) = viewModelScope.launch {
        try {
            val response = api.getMyProfile()
            if (response.isSuccessful) {
                val profileResult = response.body()
                profileResult?.let {
                    onSuccess.invoke(it.result)
                }
            } else {
                onFailure.invoke()
            }
        } catch (e: Exception) {
            onErrorAction.invoke()
        }
    }
    // 닉네임 중복 확인 요청 함수 (ChangeProfileFragment)
    fun checkDuplicateNickname(nickname: String, onSuccess: () -> Unit, onFailure: () -> Unit, onErrorAction: () -> Unit) = viewModelScope.launch {
        try {
            val response = api.checkDuplicateNickname(nickname)
            if (response.isSuccessful) {
                onSuccess.invoke()
            } else {
                onFailure.invoke()
            }
        } catch(e: Exception) {
            onErrorAction.invoke()
        }
    }
    // 프로필 수정 함수 (ChangeProfileFragment)
    fun changeMyProfile(file: MultipartBody.Part?, changeProfileData: ChangeProfileData, onSuccess: () -> Unit, onFailure: () -> Unit, onErrorAction: () -> Unit) = viewModelScope.launch {
        val json = Gson().toJson(changeProfileData) // ChangeProfileData를 JSON으로 직렬화
        // ChangeProfileData를 RequestBody로 변환
        val requestBody = json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        val response = api.changeMyProfile(file, requestBody)
        try {
            if (response.isSuccessful) {
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
                    onSuccess.invoke(it.result)
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
                    onSuccess.invoke(it.result)
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
    // 검색 결과 저장하는 함수
    private fun setCurrentSearchQuery(keyword: String, genreIdList: List<Int>, numOfPlayer: Int) {
        currentSearchKeyword = keyword
        currentGenreIdList = genreIdList
        currentNumOfPlayer = numOfPlayer
    }
    // 검색 결과 return하는 함수
    fun getCurrentSearchQuery(): Triple<String, List<Int>, Int> {
        return Triple(currentSearchKeyword, currentGenreIdList, currentNumOfPlayer)
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
    // 친구 요청 시 사용자 유효성 확인
    fun checkFriendRequest(nickname: String, onSuccess: (id: Int) -> Unit, onFailure: () -> Unit, onErrorAction: () -> Unit) = viewModelScope.launch {
        try {
            val response = api.checkFriendRequest(nickname)
            if (response.isSuccessful) {
                val checkResult = response.body()
                if (checkResult != null) {
                    onSuccess.invoke(checkResult.result.id)
                }
            } else {
                onFailure.invoke()
            }
        } catch(e: Exception) {
            onErrorAction.invoke()
        }
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
    // 채팅방 목록 가져오기 (ChatListFragment)
    fun getChatList(onSuccess: (chatList: List<ChatRoom>) -> Unit, onFailure: () -> Unit, onErrorAction: () -> Unit) = viewModelScope.launch {
        try {
            val response = api.getChatList()
            if (response.isSuccessful) {
                val chatListResult = response.body()
                if (chatListResult != null) {
                    onSuccess.invoke(chatListResult.result)
                }
            } else {
                onFailure.invoke()
            }
        } catch(e: Exception) {
            onErrorAction.invoke()
        }
    }
    // 개인 추천 컨텐츠 가져오기 (HomeFragment)
    fun getPersonalRecommend(onSuccess: (personalRecResultData: PersonalRecResultData) -> Unit, onFailure: () -> Unit, onErrorAction: () -> Unit) = viewModelScope.launch {
        try {
            val response = api.getPersonalRecommend()
            if (response.isSuccessful) {
                val personalRecResult = response.body()
                personalRecResult?.let {
                    onSuccess.invoke(it.result)
                }
            } else {
                onFailure.invoke()
            }
        } catch(e: Exception) {
            onErrorAction.invoke()
        }
    }
    // 구글 로그인 여부 확인하는 함수
    fun checkGoogleLogined() = viewModelScope.launch {
        val getGoogleLogined = myDataStore.getGoogleLogined()
        _googleLogined.value = getGoogleLogined
    }
    // 구글 로그인 여부 변경하는 함수
    fun setGoogleLogined(isGoogleLogined: Boolean) = viewModelScope.launch {
        myDataStore.setGoogleLogined(isGoogleLogined)
        _googleLogined.value = isGoogleLogined
    }
    // 알림 허용 권한 변경하는 함수
    fun setNotificationAllowed(isChecked: Boolean) = viewModelScope.launch {
        myDataStore.setNotificationAllowed(isChecked)
    }
    // 알림 허용 권한 여부 가져오는 함수
    fun getNotificationAllowed(): Boolean {
        return runBlocking { myDataStore.getNotificationAllowed() }
    }
    // 그룹 매칭 요청하는 함수 (RecommendFragment)
    fun requestGroupMatch(groupMatchData: GroupMatchData, onSuccess: (groupMatchResultData: GroupMatchResultData) -> Unit, onFailure: (errorCode: Int) -> Unit, onErrorAction: () -> Unit) = viewModelScope.launch {
        try {
            val response = api.requestGroupMatch(groupMatchData)
            if (response.isSuccessful) {
                val groupMatchResult = response.body()
                groupMatchResult?.let {
                    onSuccess.invoke(it.result)
                }
            } else {
                onFailure.invoke(response.code())
            }
        } catch(e: Exception) {
            onErrorAction.invoke()
        }
    }
    // 그룹 추천 결과 요청하는 함수 (MatchFragment)
    fun requestGroupRecommend(groupRecommendData: GroupRecommendData, onSuccess: (groupRecommendResultData: GroupRecommendResultData) -> Unit, onFailure: () -> Unit, onErrorAction: () -> Unit) = viewModelScope.launch {
        try {
            val response = api.requestGroupRecommend(groupRecommendData)
            if (response.isSuccessful) {
                val groupRecommendResult = response.body()
                groupRecommendResult?.let {
                    onSuccess.invoke(it.result)
                }
            } else {
                onFailure.invoke()
            }
        } catch(e: Exception) {
            onErrorAction.invoke()
        }
    }
    // 추천 받았던 보드게임 요청하는 함수 (RecListFragment)
    fun requestRecommendedList(onSuccess: (groupRecommendResultData: GroupRecommendResultData) -> Unit, onFailure: (errorCode: Int) -> Unit, onErrorAction: () -> Unit) = viewModelScope.launch {
        try {
            val response = api.requestRecommendedList()
            if (response.isSuccessful) {
                val groupRecommendResult = response.body()
                groupRecommendResult?.let {
                    onSuccess.invoke(it.result)
                }
            } else {
                onFailure.invoke(response.code())
            }
        } catch(e: Exception) {
            onErrorAction.invoke()
        }
    }
    // 채팅 대화 페이징 (ChatFragment)
    fun getChatting(id: Int, pageNum: Int?, size: Int, onSuccess: (chatResultData: ChatResultData) -> Unit, onFailure: () -> Unit, onErrorAction: () -> Unit) = viewModelScope.launch {
        try {
            val response = api.getChatting(id, pageNum, size)
            if (response.isSuccessful) {
                val chatResult = response.body()
                chatResult?.let {
                    onSuccess.invoke(it.result)
                }
            }
        } catch(e: Exception) {
            onErrorAction.invoke()
        }
    }
}