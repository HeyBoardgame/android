package com.project.heyboardgame.dataModel

import com.google.gson.annotations.SerializedName

data class ChangeProfileData (
    @SerializedName("nickname")
    val nickname : String,
    @SerializedName("isChanged")
    var isChanged : Boolean
)