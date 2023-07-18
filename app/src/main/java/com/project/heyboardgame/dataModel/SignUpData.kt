package com.project.heyboardgame.dataModel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SignUpData(
    val email : String,
    val password : String,
    val nickname : String,
    val id : List<String>
) : Parcelable

