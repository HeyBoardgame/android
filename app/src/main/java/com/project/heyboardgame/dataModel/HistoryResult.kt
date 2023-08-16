package com.project.heyboardgame.dataModel

import com.google.gson.annotations.SerializedName

data class HistoryResult (
    @SerializedName("result")
    val result: HistoryResultData
)

data class HistoryResultData (
    @SerializedName("content")
    val boardGames: List<BoardGame>,
    @SerializedName("prevPage")
    val prevPage: Int?,
    @SerializedName("nextPage")
    val nextPage: Int?
)