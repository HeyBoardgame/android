package com.project.heyboardgame.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.project.heyboardgame.R
import com.project.heyboardgame.dataModel.ChatRoom
import com.project.heyboardgame.dataModel.Friend
import com.project.heyboardgame.utils.GlideUtils
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ChatListRVAdapter(var chatRoomList: MutableList<ChatRoom>) : RecyclerView.Adapter<ChatListRVAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image = view.findViewById<ImageView>(R.id.profileImg)
        val nickname = view.findViewById<TextView>(R.id.nickname)
        val lastMessage = view.findViewById<TextView>(R.id.lastMessage)
        val timestamp = view.findViewById<TextView>(R.id.timestamp)
        val unread = view.findViewById<CardView>(R.id.unread)
        val unreadNum = view.findViewById<TextView>(R.id.unreadNum)
    }

    fun addAll(newList: MutableList<ChatRoom>) {
        chatRoomList.addAll(newList)
        notifyItemRangeChanged(0, chatRoomList.size)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addNewRoom(roomId: Long, friend: Friend, lastMessage: String, timestamp: String) {
        val foundItem = chatRoomList.find { it.roomId == roomId }

        Timber.d("$foundItem")

        if (foundItem != null) { // 기존 채팅방이 존재하는 경우
            val position = chatRoomList.indexOf(foundItem)
            if (position != 0) {
                val newRoom = ChatRoom(roomId, friend, lastMessage, timestamp, foundItem.unreadMessage?.let { it + 1 } ?: 1)
                chatRoomList.removeAt(position)
                chatRoomList.add(0, newRoom)
                notifyDataSetChanged()
            } else { // 기존 채팅방이 맨 위에 있는 경우
                val newRoom = ChatRoom(roomId, friend, lastMessage, timestamp, foundItem.unreadMessage?.let { it + 1 } ?: 1)
                chatRoomList.removeAt(0)
                chatRoomList.add(0, newRoom)
                notifyDataSetChanged()
            }
        } else { // 새로운 채팅방이 생성된 경우
            val newRoom = ChatRoom(roomId, friend, lastMessage, timestamp, 1)
            chatRoomList.add(0, newRoom)
            notifyItemInserted(0)
        }
    }

    fun deleteAndUpdateRoom(existingRoom: ChatRoom) {
        val position = chatRoomList.indexOf(existingRoom)
        if (position != -1) {
            chatRoomList.removeAt(position)
            chatRoomList.add(0, existingRoom)
            notifyItemMoved(position, 0)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(item: ChatRoom)
    }

    private var itemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListRVAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatListRVAdapter.ViewHolder, position: Int) {
        val item = chatRoomList[position]

        item.let {
            GlideUtils.loadThumbnailImage(holder.itemView.context, it.userInfo.image, holder.image)
            holder.nickname.text = it.userInfo.nickname
            holder.lastMessage.text = it.lastMessage
            holder.timestamp.text = formatTimestamp(it.timestamp)
            if (item.unreadMessage == 0) {
                holder.unread.visibility = View.INVISIBLE
            } else {
                holder.unread.visibility = View.VISIBLE
                holder.unreadNum.text = it.unreadMessage.toString()
            }
            holder.itemView.setOnClickListener {
                itemClickListener?.onItemClick(item)
            }
        }
    }

    override fun getItemCount(): Int {
        return chatRoomList.size
    }

    private fun formatTimestamp(timestamp: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val currentDate = Calendar.getInstance().time
        val parsedDate = dateFormat.parse(timestamp)
        val currentCalendar = Calendar.getInstance()
        currentCalendar.time = currentDate
        val parsedCalendar = Calendar.getInstance()
        parsedCalendar.time = parsedDate

        return when {
            isSameDay(currentDate, parsedDate) -> {
                val timeFormat = SimpleDateFormat("a h:mm", Locale.getDefault())
                timeFormat.format(parsedDate)
            }
            isYesterday(currentDate, parsedDate) -> {
                "어제"
            }
            currentCalendar.get(Calendar.YEAR) == parsedCalendar.get(Calendar.YEAR) -> {
                val dayFormat = SimpleDateFormat("M월 d일", Locale.getDefault())
                dayFormat.format(parsedDate)
            }
            else -> {
                val yearFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
                yearFormat.format(parsedDate)
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun isSameDay(date1: Date, date2: Date): Boolean {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(date1) == dateFormat.format(date2)
    }

    @SuppressLint("SimpleDateFormat")
    private fun isYesterday(date1: Date, date2: Date): Boolean {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal1 = Calendar.getInstance()
        val cal2 = Calendar.getInstance()
        cal1.time = date1
        cal2.time = date2
        cal1.add(Calendar.DAY_OF_YEAR, -1)
        return dateFormat.format(cal1.time) == dateFormat.format(cal2.time)
    }
}