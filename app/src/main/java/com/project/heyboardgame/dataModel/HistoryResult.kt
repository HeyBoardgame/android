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

data class BoardGame (
    @SerializedName("id")
    val id: Int,
    @SerializedName("imagePath")
    val image: String,
    @SerializedName("name")
    val title: String
)