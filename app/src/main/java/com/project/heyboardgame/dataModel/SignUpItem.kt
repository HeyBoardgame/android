package com.project.heyboardgame.dataModel

data class SignUpItem(
    val genreIcon: Int,
    val genreName: String,
    val genreId: String,
    var isClicked: Boolean
) {
    fun getItemId(): String {
        return genreId
    }
}