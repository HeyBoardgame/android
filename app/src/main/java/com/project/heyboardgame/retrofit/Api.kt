package com.project.heyboardgame.retrofit

import com.project.heyboardgame.dataModel.LoginData
import com.project.heyboardgame.dataModel.LoginResult
import com.project.heyboardgame.dataModel.SignUpData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface Api {

    @POST("/auths/login") // 로그인
    fun requestLogin(@Body userInfo: LoginData) : Call<LoginResult>

    @POST("/auths/refresh") // refreshToken 요청
    fun getNewToken(@Body refreshToken : String) : Call<String>

    @POST("/auths/temp-password") // 임시 비밀번호 발급
    fun requestNewPassword(@Body email : String) : Call<Void>

    @GET("/auths/email-duplicate") // 이메일 중복 확인
    fun checkDuplicateEmail(@Query("email") email: String): Call<Void>

    @POST("/auths/register") // 로그인
    fun requestRegister(@Body userInfo: SignUpData) : Call<Void>

}