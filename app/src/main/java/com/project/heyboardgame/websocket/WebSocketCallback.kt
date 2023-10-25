package com.project.heyboardgame.websocket

interface WebSocketCallback {
    fun onMessageReceived(senderId: Int, message: String, timestamp: String)
}