package com.project.heyboardgame.dataModel

import com.google.gson.annotations.SerializedName

data class SearchResult(
    @SerializedName("result")
    val result: SearchResultData
)

data class SearchResultData(
    @SerializedName("content")
    val boardGames: List<BoardGame2>,
    @SerializedName("prevPage")
    val prevPage: Int?,
    @SerializedName("nextPage")
    val nextPage: Int?
)