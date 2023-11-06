package com.project.heyboardgame.dataModel

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class GoogleRegisterData (
    @SerializedName("email")
    val email: String,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("favoriteGenreIds")
    val id : List<Long>
)
@Parcelize
data class GoogleRegisterTempData (
    val email: String,
    val nickname: String
): Parcelable