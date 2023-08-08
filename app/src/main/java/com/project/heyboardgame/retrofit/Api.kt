package com.project.heyboardgame.retrofit

import com.project.heyboardgame.dataModel.ChangePasswordData
import com.project.heyboardgame.dataModel.DetailResult
import com.project.heyboardgame.dataModel.GoogleLoginData
import com.project.heyboardgame.dataModel.GoogleLoginResult
import com.project.heyboardgame.dataModel.GoogleRegisterData
import com.project.heyboardgame.dataModel.HistoryResult
import com.project.heyboardgame.dataModel.LoginData
import com.project.heyboardgame.dataModel.LoginResult
import com.project.heyboardgame.dataModel.MyProfileResult
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
    suspend fun getNewToken(@Body refreshData : RefreshData) : Response<RefreshResult>

    @PATCH("auths/temp-password") // 임시 비밀번호 발급
    suspend fun requestTempPassword(@Body tempPasswordData: TempPasswordData): Response<Void>

    @GET("auths/email-duplicate") // 이메일 중복 확인
    suspend fun checkDuplicateEmail(@Query("email") email: String): Response<Void>

    @POST("auths/register") // 회원가입
    suspend fun requestRegister(@Body signUpData: SignUpData) : Response<Void>

    @PATCH("auths/logout") // 로그아웃
    suspend fun requestLogout() : Response<Void>

    @DELETE("auths/unregister") // 회원 탈퇴
    suspend fun requestUnregister() : Response<Void>

    @PATCH("auths/new-password") // 비밀번호 재설정
    suspend fun changePassword(@Body changePasswordData: ChangePasswordData) : Response<Void>

    @GET("my") // 프로필 조회
    suspend fun requestMyProfile() : Response<MyProfileResult>

    @Multipart
    @PATCH("my") // 프로필 수정
    suspend fun changeMyProfile(@Part file: MultipartBody.Part?, @Part("data") changeProfileData: RequestBody) : Response<Void>

    @POST("oauths/login") // 구글 로그인
    suspend fun requestGoogleLogin(@Body googleLoginData: GoogleLoginData) : Response<GoogleLoginResult>

    @POST("oauths/register") // 구글 회원가입
    suspend fun requestGoogleRegister(@Body googleRegisterData: GoogleRegisterData) : Response<GoogleLoginResult>

    @GET("boardgames/") // 보드게임 검색
    suspend fun requestSearchResult(
        @Query("keyword") keyword: String,
        @Query("genreIdList") genreIdList: List<Int>,
        @Query("numOfPlayer") numOfPlayer: Int
    ): Response<SearchResult>

    @GET("boardgames/{id}") // 보드게임 단건 조회
    suspend fun requestDetail(@Path("id") id : Int) : Response<DetailResult>

    @POST("boardgames/{id}/bookmarks") // 보드게임 찜하기
    suspend fun requestBookmark(@Path("id") id : Int) : Response<Void>

    @DELETE("boardgames/{id}/bookmarks") // 보드게임 찜하기 취소
    suspend fun deleteBookmark(@Path("id") id : Int) : Response<Void>

    @PUT("boardgames/{id}/rate") // 보드게임 평가하기
    suspend fun requestRating(@Path("id") id : Int, @Body ratingData : RatingData) : Response<Void>

    @GET("my/bookmarks") // 찜한 보드게임 목록 조회
    suspend fun requestBookmarkList(@Query("order") sort: String, @Query("page") pageNum: Int?) : Response<HistoryResult>

    @GET("my/ratings") // 평가한 보드게임 목록 조회
    suspend fun requestRatedList() : Response<RatedResult>

    @GET("my/ratings/score")
    suspend fun requestSpecificRatedList(@Query("score") score: Float, @Query("order") sort: String, @Query("page") pageNum: Int?) : Response<HistoryResult>

}