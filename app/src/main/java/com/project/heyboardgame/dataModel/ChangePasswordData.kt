package com.project.heyboardgame.dataModel

import com.google.gson.annotations.SerializedName

data class ChangePasswordData (
    @SerializedName("existingPassword")
    val currentPassword : String,
    @SerializedName("updatedPassword")
    val newPassword : String
)