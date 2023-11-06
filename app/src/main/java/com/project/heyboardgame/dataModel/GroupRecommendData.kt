package com.project.heyboardgame.dataModel

import com.google.gson.annotations.SerializedName

data class GroupRecommendData (
    @SerializedName("members")
    val idList: List<Long>,
    @SerializedName("seed")
    val seed: Int
)