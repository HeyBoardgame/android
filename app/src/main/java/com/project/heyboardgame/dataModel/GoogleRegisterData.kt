package com.project.heyboardgame.dataModel

import com.google.gson.annotations.SerializedName

data class GoogleRegisterData (
    @SerializedName("email")
    val email: String,
    @SerializedName("favoriteGenreIds")
    val id : List<Int>
)