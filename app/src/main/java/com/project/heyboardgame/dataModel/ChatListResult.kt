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
    @SerializedName("roomId")
    val roomId: Int,
    @SerializedName("friendId")
    val friendId: Int,
    @SerializedName("imagePath")
    val image: String?,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("lastMessage")
    val lastMessage: String,
    @SerializedName("unreadMessage")
    val unreadMessage: Int?,
    @SerializedName("timestamp")
    val timestamp: String
)