package com.project.heyboardgame.dataModel

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class SignUpData(
    @SerializedName("email")
    val email : String,
    @SerializedName("password")
    val password : String,
    @SerializedName("nickname")
    val nickname : String,
    @SerializedName("favoriteGenreIds")
    val id : List<Long>
) : Parcelable

