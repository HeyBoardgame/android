package com.project.heyboardgame.dataModel

data class SignUpItem(
    val genreIcon: Int,
    val genreName: String,
    val genreId: Int,
    var isClicked: Boolean
) {
    fun getItemId(): Int {
        return genreId
    }
}