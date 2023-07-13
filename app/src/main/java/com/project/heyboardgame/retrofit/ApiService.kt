package com.project.heyboardgame.retrofit

import com.project.heyboardgame.retrofit.dataModel.LoginData
import com.project.heyboardgame.retrofit.dataModel.LoginResult
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("/auths/login")
    fun requestLogin(@Body userInfo: LoginData) : Call<LoginResult>

    @POST("/auths/refresh")
    fun getNewToken(@Body refreshToken : String) : Call<String>
}