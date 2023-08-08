package com.project.heyboardgame.dataModel

import com.google.gson.annotations.SerializedName

data class HistoryResult (
    @SerializedName("result")
    val result: HistoryResultData
)

data class HistoryResultData (
    @SerializedName("boardGames")
    val boardGames: List<BoardGame>
)