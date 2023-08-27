package com.project.heyboardgame.dataModel

import com.google.gson.annotations.SerializedName

data class ChatData (
    @SerializedName("msg")
    val message: String,
    @SerializedName("timeStamp")
    val timestamp: String
)