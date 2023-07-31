package com.project.heyboardgame.dataModel

import com.google.gson.annotations.SerializedName

data class SearchResult(
    @SerializedName("result")
    val result: List<SearchResultData>
)

data class SearchResultData(
    @SerializedName("boardGameId")
    val boardGameId: Int,
    @SerializedName("boardGameImg")
    val boardGameImg: String,
    @SerializedName("boardGameTitle")
    val boardGameTitle: String,
    @SerializedName("boardGameGenre")
    val boardGameGenre: List<String>,
    @SerializedName("boardGameDifficulty")
    val boardGameDifficulty: String,
    @SerializedName("boardGameNumOfPlayer")
    val boardGameNumOfPlayer: String
)