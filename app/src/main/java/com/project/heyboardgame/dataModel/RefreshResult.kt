package com.project.heyboardgame.dataModel

import com.google.gson.annotations.SerializedName

data class RefreshResult(
    @SerializedName("result")
    val result: RefreshResultData
)

data class RefreshResultData (
    @SerializedName("accessToken")
    val accessToken : String,
    @SerializedName("refreshToken")
    val refreshToken : String
)