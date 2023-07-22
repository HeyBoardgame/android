package com.project.heyboardgame.dataModel

import android.net.Uri
import com.google.gson.annotations.SerializedName

data class MyProfileResult(
    @SerializedName("result")
    val result: MyProfileResultData
)

data class MyProfileResultData (
    @SerializedName("profileImg")
    val profileImg : String,
    @SerializedName("nickname")
    val nickname : String,
    @SerializedName("friendCode")
    val friendCode : String
)