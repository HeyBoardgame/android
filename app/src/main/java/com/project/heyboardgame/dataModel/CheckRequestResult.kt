package com.project.heyboardgame.dataModel

import com.google.gson.annotations.SerializedName

data class CheckRequestResult (
    @SerializedName("result")
    val result: Friend
)