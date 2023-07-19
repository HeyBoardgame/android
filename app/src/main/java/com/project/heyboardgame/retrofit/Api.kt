package com.project.heyboardgame.retrofit

import com.project.heyboardgame.dataModel.LoginData
import com.project.heyboardgame.dataModel.LoginResult
import com.project.heyboardgame.dataModel.SignUpData
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface Api {

    @POST("/auths/login") // 로그인
    suspend fun requestLogin(@Body userInfo: LoginData): Response<LoginResult>

    @POST("/auths/refresh") // refreshToken 요청
    suspend fun getNewToken(@Body refreshToken : String) : Response<String>

    @GET("/auths/temp-password") // 임시 비밀번호 발급
    suspend fun requestTempPassword(@Query("email") email: String): Response<Void>

    @GET("/auths/email-duplicate") // 이메일 중복 확인
    suspend fun checkDuplicateEmail(@Query("email") email: String): Response<Void>

    @POST("/auths/register") // 회원가입
    suspend fun requestRegister(@Body userInfo: SignUpData) : Response<Void>

    @PATCH("/auths/logout") // 로그아웃
    suspend fun requestLogout() : Response<Void>

    @DELETE("/auths/unregister") // 회원 탈퇴
    suspend fun requestUnregister() : Response<Void>

    @PATCH("/auths/new-password") // 비밀번호 재설정
    suspend fun requestNewPassword() : Response<Void>

}