package com.project.heyboardgame.utils

import com.project.heyboardgame.dataModel.Friend

interface FriendRequestCallback {
    fun onAcceptButtonClicked(friend: Friend)
    fun onDeclineButtonClicked(friend: Friend)
}