package com.project.heyboardgame.dataModel

import com.google.gson.annotations.SerializedName

data class GoogleLoginData (
    @SerializedName("email")
    val email: String
)