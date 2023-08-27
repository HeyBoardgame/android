package com.project.heyboardgame.dataModel

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Friend (
    @SerializedName("id")
    val id: Int,
    @SerializedName("imagePath")
    val image: String?,
    @SerializedName("nickname")
    val nickname: String
) : Parcelable