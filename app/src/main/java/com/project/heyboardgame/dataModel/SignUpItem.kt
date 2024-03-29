package com.project.heyboardgame.dataModel

data class SignUpItem(
    val genreIcon: Int,
    val genreName: String,
    val genreId: Long,
    var isClicked: Boolean
) {
    fun getItemId(): Long {
        return genreId
    }
}