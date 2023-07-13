package com.project.heyboardgame.retrofit.dataModel

import com.google.gson.annotations.SerializedName

data class LoginResult (
    @SerializedName("accessToken")
    val accessToken : String?,
    @SerializedName("refreshToken")
    val refreshToken : String?
)