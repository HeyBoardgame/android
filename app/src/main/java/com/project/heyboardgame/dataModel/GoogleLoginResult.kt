package com.project.heyboardgame.dataModel

import com.google.gson.annotations.SerializedName

data class GoogleLoginResult(
    @SerializedName("resultCode")
    val resultCode: String,
    @SerializedName("result")
    val result: GoogleLoginResultData
)

data class GoogleLoginResultData(
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("refreshToken")
    val refreshToken: String
)