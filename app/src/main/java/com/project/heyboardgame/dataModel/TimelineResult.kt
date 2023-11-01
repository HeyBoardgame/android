package com.project.heyboardgame.dataModel

import com.google.gson.annotations.SerializedName

data class TimelineResult (
    @SerializedName("result")
    val result: TimelineResultData
)

data class TimelineResultData (
    @SerializedName("content")
    val content: List<Timeline>,
    @SerializedName("prevPage")
    val prevPage: Int?,
    @SerializedName("nextPage")
    val nextPage: Int?
)

data class Timeline (
    @SerializedName("id")
    val timelineId: Int,
    @SerializedName("memberProfileImages")
    val memberProfileImages: List<String?>,
    @SerializedName("recommendedAt")
    val recommendedAt: String,
    @SerializedName("isMemberMoreThanLimit")
    val isMemberMoreThanLimit: Boolean
)

