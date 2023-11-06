package com.project.heyboardgame.sse

import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.project.heyboardgame.dataModel.ChatRoom
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import timber.log.Timber

class MyEventSourceListener : EventSourceListener() {
    override fun onOpen(eventSource: EventSource, response: Response) {
        super.onOpen(eventSource, response)
        Timber.d("SSE Open")
    }

    override fun onClosed(eventSource: EventSource) {
        super.onClosed(eventSource)
        Timber.d("SSE Closed")
    }

    override fun onEvent(eventSource: EventSource, id: String?, type: String?, data: String) {
        super.onEvent(eventSource, id, type, data)
        Timber.d("$id, $type, $data")
        try {
            val gson = Gson()
            val chatRoom: ChatRoom = gson.fromJson(data, ChatRoom::class.java)
            Timber.d("$chatRoom")
        } catch(e: JsonParseException) {
            Timber.d("JSON 파싱 오류: ${e.localizedMessage}")
        }
//        val gson = Gson()
//        val chatRoom: ChatRoom = gson.fromJson(data, ChatRoom::class.java)
//
//        if (chatRoom.unreadMessage == 0) {
//            chatRoom.unreadMessage = 1
//        } else {
//            chatRoom.unreadMessage = chatRoom.unreadMessage?.plus(1)
//        }
//
//        if (currentChatRoomList.contains(chatRoom)) {
//            // 현재 채팅방 목록에 있던 채팅방 일 때
//            currentChatRoomList.filterNot {
//                it.roomId == chatRoom.roomId
//            }
//            currentChatRoomList = listOf(chatRoom) + currentChatRoomList
//            chatListRVAdapter.updateData(currentChatRoomList)
//        } else {
//            // 현재 채팅방 목록에 없던 새로운 채팅방 일 때
//            currentChatRoomList = listOf(chatRoom) + currentChatRoomList
//            chatListRVAdapter.updateData(currentChatRoomList)
//        }
    }

    override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
        super.onFailure(eventSource, t, response)
        Timber.d("SSE Failed: $t, $response")
    }
}