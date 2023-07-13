package com.project.heyboardgame.retrofit

import com.project.heyboardgame.retrofit.dataModel.LoginData
import com.project.heyboardgame.retrofit.dataModel.LoginResult
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface Api {

    @POST("/auths/login")
    fun requestLogin(@Body userInfo: LoginData) : Call<LoginResult>

    @POST("/auths/refresh")
    fun getNewToken(@Body refreshToken : String) : Call<String>

    @POST("/auths/temp-password")
    fun requestNewPassword(@Body email : String) : Call<String>
}