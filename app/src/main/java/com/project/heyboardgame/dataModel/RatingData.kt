package com.project.heyboardgame.dataModel

import com.google.gson.annotations.SerializedName

data class RatingData (
    @SerializedName("rate")
    val rating : Float
)