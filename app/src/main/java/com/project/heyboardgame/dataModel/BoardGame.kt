package com.project.heyboardgame.dataModel

import com.google.gson.annotations.SerializedName

data class BoardGame (
    @SerializedName("id")
    val id: Int,
    @SerializedName("imagePath")
    val image: String,
    @SerializedName("name")
    val title: String
)