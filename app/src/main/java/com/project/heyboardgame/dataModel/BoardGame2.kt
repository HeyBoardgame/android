package com.project.heyboardgame.dataModel

import com.google.gson.annotations.SerializedName

data class BoardGame2 (
    @SerializedName("id")
    val id: Int,
    @SerializedName("imagePath")
    val image: String,
    @SerializedName("name")
    val title: String,
    @SerializedName("genre")
    val genre: List<String>,
    @SerializedName("weight")
    val difficulty: String,
    @SerializedName("playerMax")
    val playerMax : Int,
    @SerializedName("playerMin")
    val playerMin : Int
)