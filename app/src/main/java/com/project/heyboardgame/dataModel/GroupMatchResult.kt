package com.project.heyboardgame.dataModel

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class GroupMatchResult (
    @SerializedName("result")
    val result: GroupMatchResultData
)

@Parcelize
data class GroupMatchResultData (
    @SerializedName("group")
    val group: List<Friend>,
    @SerializedName("seed")
    val seed: Int
) : Parcelable