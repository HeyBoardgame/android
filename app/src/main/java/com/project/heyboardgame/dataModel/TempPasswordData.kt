package com.project.heyboardgame.dataModel

import com.google.gson.annotations.SerializedName

data class TempPasswordData (
    @SerializedName("email")
    val email: String
)