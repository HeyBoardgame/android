package com.project.heyboardgame.dataModel

import com.google.gson.annotations.SerializedName

data class GroupMatchData (
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double
)