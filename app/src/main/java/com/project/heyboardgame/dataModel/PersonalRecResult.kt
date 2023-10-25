package com.project.heyboardgame.dataModel

import com.google.gson.annotations.SerializedName

data class PersonalRecResult (
    @SerializedName("result")
    val result: PersonalRecResultData
)

data class PersonalRecResultData (
    @SerializedName("keys")
    val keys: List<String>,
    @SerializedName("shelves")
    val shelves: Map<String, List<BoardGame>>,
    @SerializedName("descriptions")
    val descriptions: Map<String, String>
)