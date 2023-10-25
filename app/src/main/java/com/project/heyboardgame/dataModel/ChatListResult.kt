package com.project.heyboardgame.dataModel

import com.google.gson.annotations.SerializedName

data class ChatListResult (
    @SerializedName("result")
    val result: List<ChatRoom>
)

data class ChatRoom (
    @SerializedName("id")
    val roomId: Int,
    @SerializedName("userInfo")
    val userInfo: Friend,
    @SerializedName("lastMessage")
    val lastMessage: String,
    @SerializedName("createdAt")
    val timestamp: String,
    @SerializedName("unReadMessage")
    var unreadMessage: Int?
)