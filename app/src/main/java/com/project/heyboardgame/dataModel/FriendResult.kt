package com.project.heyboardgame.dataModel

import com.google.gson.annotations.SerializedName

data class FriendResult (
    @SerializedName("result")
    val result: FriendResultData
)

data class FriendResultData (
    @SerializedName("content")
    val friends: List<Friend>,
    @SerializedName("prevPage")
    val prevPage: Int?,
    @SerializedName("nextPage")
    val nextPage: Int?
)