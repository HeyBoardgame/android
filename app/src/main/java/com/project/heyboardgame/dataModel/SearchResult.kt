package com.project.heyboardgame.dataModel

import com.google.gson.annotations.SerializedName

data class SearchResult(
    @SerializedName("result")
    val result: SearchResultData
)

data class SearchResultData(
    @SerializedName("boardGames")
    val boardGames: List<BoardGame2>
)