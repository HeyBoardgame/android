package com.project.heyboardgame.dataModel

import com.google.gson.annotations.SerializedName

data class RoomIdResult (
    @SerializedName("result")
    val result: RoomId
)

data class RoomId (
    @SerializedName("id")
    val roomId: Long
)