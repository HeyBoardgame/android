package com.project.heyboardgame.dataModel

import com.google.gson.annotations.SerializedName

data class RatingData (
    @SerializedName("score")
    val rating : Float
)