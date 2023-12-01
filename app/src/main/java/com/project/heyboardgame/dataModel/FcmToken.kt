package com.project.heyboardgame.dataModel

import com.google.gson.annotations.SerializedName

data class FcmToken (
    @SerializedName("token")
    val fcmToken: String
)