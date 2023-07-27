package com.project.heyboardgame.dataModel

import com.google.gson.annotations.SerializedName

data class DetailResult(
    @SerializedName("result")
    val result: DetailResultData
)

data class DetailResultData (
    @SerializedName("starRating")
    val starRating : Double,
    @SerializedName("genre")
    var genre : List<String>,
    @SerializedName("theme")
    var theme : List<String>,
    @SerializedName("title")
    val title : String,
    @SerializedName("difficulty")
    val difficulty : String,
    @SerializedName("timeRequired")
    val timeRequired : String,
    @SerializedName("numOfPeople")
    val numOfPeople : String,
    @SerializedName("description")
    val description : String,
    @SerializedName("strategy")
    val strategy : String,
    @SerializedName("isBookmarked")
    val isBookmarked : Boolean,
    @SerializedName("myRating")
    val myRating : Int

)