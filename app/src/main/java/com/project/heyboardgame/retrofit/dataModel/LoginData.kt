package com.project.heyboardgame.retrofit.dataModel

import com.google.gson.annotations.SerializedName

data class LoginData (
    @SerializedName("email")
    val userId : String,
    @SerializedName("password")
    val userPw : String
)