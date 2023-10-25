package com.project.heyboardgame.retrofit

import com.project.heyboardgame.dataModel.ChangePasswordData
import com.project.heyboardgame.dataModel.ChatListResult
import com.project.heyboardgame.dataModel.ChatResult
import com.project.heyboardgame.dataModel.CheckRequestResult
import com.project.heyboardgame.dataModel.DetailResult
import com.project.heyboardgame.dataModel.FcmToken
import com.project.heyboardgame.dataModel.FriendRequestData
import com.project.heyboardgame.dataModel.FriendResult
import com.project.heyboardgame.dataModel.GoogleLoginData
import com.project.heyboardgame.dataModel.GoogleLoginResult
import com.project.heyboardgame.dataModel.GoogleRegisterData
import com.project.heyboardgame.dataModel.GroupMatchData
import com.project.heyboardgame.dataModel.GroupMatchResult
import com.project.heyboardgame.dataModel.GroupRecommendData
import com.project.heyboardgame.dataModel.GroupRecommendResult
import com.project.heyboardgame.dataModel.HistoryResult
import com.project.heyboardgame.dataModel.LoginData
import com.project.heyboardgame.dataModel.LoginResult
import com.project.heyboardgame.dataModel.MyProfileResult
import com.project.heyboardgame.dataModel.PersonalRecResult
import com.project.heyboardgame.dataModel.RatedResult
import com.project.heyboardgame.dataModel.RatingData
import com.project.heyboardgame.dataModel.RefreshData
import com.project.heyboardgame.dataModel.RefreshResult
import com.project.heyboardgame.dataModel.SearchResult
import com.project.heyboardgame.dataModel.SignUpData
import com.project.heyboardgame.dataModel.TempPasswordData
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface Api {
    @POST("auths/login") // 로그인
    suspend fun requestLogin(@Body loginData: LoginData): Response<LoginResult>

    @POST("auths/refresh") // refreshToken 요청
    suspend fun getNewToken(@Body refreshData: RefreshData): Response<RefreshResult>

    @PATCH("auths/temp-password") // 임시 비밀번호 발급
    suspend fun requestTempPassword(@Body tempPasswordData: TempPasswordData): Response<Void>

    @GET("auths/email-duplicate") // 이메일 중복 확인
    suspend fun checkDuplicateEmail(@Query("email") email: String): Response<Void>

    @GET("auths/nickname-duplicate") // 닉네임 중복 확인
    suspend fun checkDuplicateNickname(@Query("nickname") nickname: String): Response<Void>

    @POST("auths/register") // 회원가입
    suspend fun requestRegister(@Body signUpData: SignUpData): Response<Void>

    @PATCH("auths/logout") // 로그아웃
    suspend fun requestLogout(): Response<Void>

    @DELETE("auths/unregister") // 회원 탈퇴
    suspend fun requestUnregister(): Response<Void>

    @PATCH("auths/new-password") // 비밀번호 재설정
    suspend fun changePassword(@Body changePasswordData: ChangePasswordData): Response<Void>

    @GET("my") // 프로필 조회
    suspend fun getMyProfile(): Response<MyProfileResult>

    @Multipart
    @PATCH("my") // 프로필 수정
    suspend fun changeMyProfile(@Part file: MultipartBody.Part?, @Part("data") changeProfileData: RequestBody): Response<Void>

    @POST("oauths/login") // 구글 로그인
    suspend fun requestGoogleLogin(@Body googleLoginData: GoogleLoginData): Response<GoogleLoginResult>

    @POST("oauths/register") // 구글 회원가입
    suspend fun requestGoogleRegister(@Body googleRegisterData: GoogleRegisterData): Response<GoogleLoginResult>

    @GET("boardgames/search") // 보드게임 검색
    suspend fun getSearchResult(
        @Query("searchWord") keyword: String,
        @Query("genre") genreIdList: List<Int>,
        @Query("player") numOfPlayer: Int,
        @Query("page") pageNum: Int?,
        @Query("size") size: Int
    ): Response<SearchResult>

    @GET("boardgames/{id}") // 보드게임 단건 조회
    suspend fun getDetail(@Path("id") id: Int): Response<DetailResult>

    @POST("boardgames/{id}/bookmarks") // 보드게임 찜하기
    suspend fun addBookmark(@Path("id") id: Int): Response<Void>

    @DELETE("boardgames/{id}/bookmarks") // 보드게임 찜하기 취소
    suspend fun deleteBookmark(@Path("id") id: Int): Response<Void>

    @PUT("boardgames/{id}/rate") // 보드게임 평가하기
    suspend fun requestRating(@Path("id") id: Int, @Body ratingData: RatingData): Response<Void>

    @GET("my/bookmarks") // 찜한 보드게임 목록 조회
    suspend fun getBookmarkList(@Query("sort") sort: String, @Query("page") pageNum: Int?, @Query("size") size: Int): Response<HistoryResult>

    @GET("my/ratings") // 평가한 보드게임 목록 조회
    suspend fun getRatedList(): Response<RatedResult>

    @GET("my/ratings/score") // 특정 평점 보드게임 목록 조회
    suspend fun getSpecificRatedList(
        @Query("score") score: Float,
        @Query("sort") sort: String,
        @Query("page") pageNum: Int?,
        @Query("size") size: Int
    ): Response<HistoryResult>

    @GET("friends") // 친구 목록 조회
    suspend fun getFriendList(@Query("page") pageNum: Int?, @Query("size") size: Int): Response<FriendResult>

    @GET("friends/requests") // 친구 요청 목록 조회
    suspend fun getFriendRequestList(@Query("page") pageNum: Int?, @Query("size") size: Int): Response<FriendResult>

    @GET("friends/requests") // 상위 n개 친구 요청 목록 조회
    suspend fun getFriendRequestList(): Response<FriendResult>

    @POST("friends/requests/{id}") // 친구 요청 수락
    suspend fun acceptFriendRequest(@Path("id") id: Int): Response<Void>

    @DELETE("friends/requests/{id}") // 친구 요청 거절
    suspend fun declineFriendRequest(@Path("id") id: Int): Response<Void>

    @GET("friends/requests/search") // 친구 요청 시 사용자 유효성 확인
    suspend fun checkFriendRequest(@Query("nickname") nickname: String): Response<CheckRequestResult>

    @POST("friends/requests") // 친구 요청 보내기
    suspend fun sendFriendRequest(@Body friendRequestData: FriendRequestData): Response<Void>

    @GET("chats") // 채팅방 목록 조회
    suspend fun getChatList(): Response<ChatListResult>

    @GET("chats/{id}") // 채팅방 대화 조회
    suspend fun getChatting(@Path("id") id: Int, @Query("page") pageNum: Int?, @Query("size") size: Int): Response<ChatResult>

    @GET("recommends") // 홈화면 컨텐츠(개인추천) 조회
    suspend fun getPersonalRecommend(): Response<PersonalRecResult>

    @PATCH("auths/fcm-token") // 기기 토큰 등록
    suspend fun sendFcmToken(@Body fcmToken: FcmToken): Response<Void>

    @POST("recommends/group-match") // 그룹 매칭 요청
    suspend fun requestGroupMatch(@Body groupMatchData: GroupMatchData): Response<GroupMatchResult>

    @POST("recommends/group") // 그룹 추천 결과 요청
    suspend fun requestGroupRecommend(@Body groupRecommendData: GroupRecommendData): Response<GroupRecommendResult>

    @GET("recommends/group/history") // 그룹 추천 기록 조회
    suspend fun requestRecommendedList(): Response<GroupRecommendResult>
}