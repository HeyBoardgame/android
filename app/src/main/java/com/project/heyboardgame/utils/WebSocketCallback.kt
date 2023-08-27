package com.project.heyboardgame.utils

interface WebSocketCallback {
    fun onMessageReceived(senderId: Int, message: String, timestamp: String)
}