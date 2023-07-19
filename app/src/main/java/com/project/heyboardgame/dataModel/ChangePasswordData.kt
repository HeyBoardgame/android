package com.project.heyboardgame.dataModel

import com.google.gson.annotations.SerializedName

data class ChangePasswordData (
    @SerializedName("currentPassword")
    val currentPassword : String,
    @SerializedName("newPassword")
    val newPassword : String
)