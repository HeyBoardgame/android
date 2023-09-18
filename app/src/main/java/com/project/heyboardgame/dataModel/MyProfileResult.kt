package com.project.heyboardgame.dataModel

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class MyProfileResult(
    @SerializedName("result")
    val result: MyProfileResultData
)

@Parcelize
data class MyProfileResultData (
    @SerializedName("profileImagePath")
    val profileImg : String?,
    @SerializedName("nickname")
    val nickname : String
) : Parcelable