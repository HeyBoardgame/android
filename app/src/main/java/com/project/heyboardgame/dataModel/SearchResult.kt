package com.project.heyboardgame.dataModel

import com.google.gson.annotations.SerializedName

data class SearchResult(
    @SerializedName("result")
    val result: List<SearchResultData>
)

data class SearchResultData(
    @SerializedName("boardGameId")
    val id: Int,
    @SerializedName("boardGameImg")
    val image: String,
    @SerializedName("boardGameTitle")
    val title: String,
    @SerializedName("boardGameGenre")
    val genre: List<String>,
    @SerializedName("boardGameDifficulty")
    val difficulty: String,
    @SerializedName("boardGameNumOfPlayer")
    val numOfPlayer: String
)