package com.project.heyboardgame.dataModel

import com.google.gson.annotations.SerializedName

data class ChatListResult (
    @SerializedName("result")
    val result: ChatListResultData
)

data class ChatListResultData (
    @SerializedName("content")
    val rooms: List<ChatRoom>,
    @SerializedName("prevPage")
    val prevPage: Int?,
    @SerializedName("nextPage")
    val nextPage: Int?
)

data class ChatRoom (
    @SerializedName("id")
    val roomId: Int,
    @SerializedName("lastMessage")
    val lastMessage: String,
    @SerializedName("timestamp")
    val timestamp: String,
    @SerializedName("userInfo")
    val userInfo: Friend
)