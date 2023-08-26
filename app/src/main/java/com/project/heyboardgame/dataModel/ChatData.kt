package com.project.heyboardgame.dataModel

import com.google.gson.annotations.SerializedName

data class ChatData (
    @SerializedName("msg")
    val message: String,
    @SerializedName("targetUserId")
    val friendId: Int
)