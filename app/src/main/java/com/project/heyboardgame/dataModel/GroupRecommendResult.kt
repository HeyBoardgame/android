package com.project.heyboardgame.dataModel

import com.google.gson.annotations.SerializedName

data class GroupRecommendResult (
    @SerializedName("result")
    val result: GroupRecommendResultData
)

data class GroupRecommendResultData (
    @SerializedName("recommendations")
    val recommendations: List<BoardGame2>
)