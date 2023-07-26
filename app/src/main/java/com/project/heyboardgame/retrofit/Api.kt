package com.project.heyboardgame.retrofit

import com.project.heyboardgame.dataModel.ChangePasswordData
import com.project.heyboardgame.dataModel.LoginData
import com.project.heyboardgame.dataModel.LoginResult
import com.project.heyboardgame.dataModel.MyProfileResult
import com.project.heyboardgame.dataModel.RefreshData
import com.project.heyboardgame.dataModel.RefreshResult
import com.project.heyboardgame.dataModel.SignUpData
import com.project.heyboardgame.dataModel.TempPasswordData
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface Api {

    @POST("/auths/login") // 로그인
    suspend fun requestLogin(@Body userInfo: LoginData): Response<LoginResult>


    @POST("/auths/refresh") // refreshToken 요청
    suspend fun getNewToken(@Body refreshData : RefreshData) : Response<RefreshResult>

    @PATCH("/auths/temp-password") // 임시 비밀번호 발급
    suspend fun requestTempPassword(@Body tempPasswordData: TempPasswordData): Response<Void>

    @GET("/auths/email-duplicate") // 이메일 중복 확인
    suspend fun checkDuplicateEmail(@Query("email") email: String): Response<Void>

    @POST("/auths/register") // 회원가입
    suspend fun requestRegister(@Body userInfo: SignUpData) : Response<Void>

    @PATCH("/auths/logout") // 로그아웃
    suspend fun requestLogout() : Response<Void>

    @DELETE("/auths/unregister") // 회원 탈퇴
    suspend fun requestUnregister() : Response<Void>

    @PATCH("/auths/new-password") // 비밀번호 재설정
    suspend fun changePassword(@Body userInfo: ChangePasswordData) : Response<Void>

    @GET("/my") // 프로필 조회
    suspend fun requestMyProfile() : Response<MyProfileResult>

    @Multipart
    @PATCH("/my") // 프로필 수정
    suspend fun changeMyProfile(@Part file: MultipartBody.Part?, @Part("data") changeProfileData: RequestBody) : Response<Void>

}