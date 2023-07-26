package com.project.heyboardgame.dataModel

import com.google.gson.annotations.SerializedName

data class MyProfileResult(
    @SerializedName("result")
    val result: MyProfileResultData
)

data class MyProfileResultData (
    @SerializedName("profileImagePath")
    val profileImg : String,
    @SerializedName("nickname")
    val nickname : String,
    @SerializedName("userCode")
    val userCode : String
)