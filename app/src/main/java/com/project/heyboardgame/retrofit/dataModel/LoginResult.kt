package com.project.heyboardgame.retrofit.dataModel

import com.google.gson.annotations.SerializedName


data class LoginResult(
    @SerializedName("result")
    val result: LoginResultData
)

data class LoginResultData(
    @SerializedName("accessToken")
    val accessToken: String?,
    @SerializedName("refreshToken")
    val refreshToken: String?
)