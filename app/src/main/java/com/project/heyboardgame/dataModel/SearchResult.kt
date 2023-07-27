package com.project.heyboardgame.dataModel

import com.google.gson.annotations.SerializedName

data class SearchResult(
    @SerializedName("result")
    val result: SearchResultData
)

data class SearchResultData (
    @SerializedName("boardGameImg")
    val boardGameImg : String,
    @SerializedName("boardGameTitle")
    val boardGameTitle : String,
    @SerializedName("boardGameGenre")
    val boardGameGenre : List<String>,
    @SerializedName("boardGameDifficulty")
    val boardGameDifficulty : String,
    @SerializedName("boardGameNumOfPeople")
    val boardGameNumOfPeople : String
)