package com.project.heyboardgame.dataModel

import com.google.gson.annotations.SerializedName

data class RefreshData (
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("refreshToken")
    val refreshToken: String
)