package com.project.heyboardgame.dataModel

import com.google.gson.annotations.SerializedName

data class RatedResult(
    @SerializedName("result")
    val result: RatedResultData
)

data class RatedResultData (
    @SerializedName("boardGames")
    val boardGames: Map<Float, List<BoardGame>>
)