package com.project.heyboardgame.dataModel

import com.google.gson.annotations.SerializedName

data class ChatResult (
    @SerializedName("result")
    val result: ChatResultData
)

data class ChatResultData (
    @SerializedName("content")
    val rooms: List<Chat>,
    @SerializedName("prevPage")
    val prevPage: Int?,
    @SerializedName("nextPage")
    val nextPage: Int?
)

data class Chat (
    @SerializedName("id") // 메세지 고유 값
    val id: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("createdAt")
    val timestamp: String,
    @SerializedName("isMyMessage")
    val isMyMessage: Boolean
)