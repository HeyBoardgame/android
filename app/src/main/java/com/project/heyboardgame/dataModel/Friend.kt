package com.project.heyboardgame.dataModel

import com.google.gson.annotations.SerializedName

data class Friend (
    @SerializedName("id")
    val id: Int,
    @SerializedName("imagePath")
    val image: String?,
    @SerializedName("nickname")
    val nickname: String
)