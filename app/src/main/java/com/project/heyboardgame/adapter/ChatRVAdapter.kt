package com.project.heyboardgame.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.heyboardgame.R
import com.project.heyboardgame.dataModel.Chat
import com.project.heyboardgame.dataModel.Friend
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

class ChatRVAdapter(var chatList: MutableList<Chat>, private val friend: Friend) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    fun addAll(newList: MutableList<Chat>) {
        chatList.addAll(newList)
        notifyItemRangeChanged(0, chatList.size)
    }

    fun add(newChat: Chat) {
        chatList.add(0, newChat)
        notifyItemRangeChanged(0, chatList.size)
    }

    companion object {
        private const val VIEW_TYPE_MY_MESSAGE = 1
        private const val VIEW_TYPE_FRIEND_MESSAGE = 2
    }

    inner class MyMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val myMessage = view.findViewById<TextView>(R.id.myMessage)
        val myMessageTime = view.findViewById<TextView>(R.id.myMessageTime)
        val dateSeparator = view.findViewById<ConstraintLayout>(R.id.dateSeparator)
        val date = view.findViewById<TextView>(R.id.date)
    }

    inner class FriendMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val friendProfileImg = view.findViewById<ImageView>(R.id.friendProfileImg)
        val friendNickname = view.findViewById<TextView>(R.id.friendNickname)
        val friendMessage = view.findViewById<TextView>(R.id.friendMessage)
        val friendMessageTime = view.findViewById<TextView>(R.id.friendMessageTime)
        val dateSeparator = view.findViewById<ConstraintLayout>(R.id.dateSeparator)
        val date = view.findViewById<TextView>(R.id.date)
    }

    override fun getItemViewType(position: Int): Int {
        val item = chatList[position]
        return if (item?.isMyMessage!!) {
            VIEW_TYPE_MY_MESSAGE
        } else {
            VIEW_TYPE_FRIEND_MESSAGE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_MY_MESSAGE) {
            val view = inflater.inflate(R.layout.item_my_message, parent, false)
            MyMessageViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.item_friend_message, parent, false)
            FriendMessageViewHolder(view)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = chatList[position]
        val nextItem = if (position + 1 < itemCount) chatList[position + 1] else null

        when (holder) {
            is ChatRVAdapter.MyMessageViewHolder -> {
                item.let {
                    holder.myMessage.text = item.message
                    holder.myMessageTime.text = formatChatTimestamp(item.timestamp)

                    if (shouldShowDateSeparator(item, nextItem)) {
                        holder.dateSeparator.visibility = View.VISIBLE
                        holder.date.text = formatDateSeparator(item.timestamp)
                    } else {
                        holder.dateSeparator.visibility = View.GONE
                    }
                }
            }
            is ChatRVAdapter.FriendMessageViewHolder -> {
                item.let {
                    if (friend.image != null) {
                        Glide.with(holder.itemView.context).load(friend.image).into(holder.friendProfileImg)
                    } else {
                        holder.friendProfileImg.setImageResource(R.drawable.default_profile_img)
                    }
                    holder.friendNickname.text = friend.nickname
                    holder.friendMessage.text = item.message
                    holder.friendMessageTime.text = formatChatTimestamp(item.timestamp)
                    if (shouldShowDateSeparator(item, nextItem)) {
                        holder.dateSeparator.visibility = View.VISIBLE
                        holder.date.text = formatDateSeparator(item.timestamp)
                    } else {
                        holder.dateSeparator.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun shouldShowDateSeparator(currentItem: Chat?, nextItem: Chat?): Boolean {
        if (nextItem == null) {
            return true
        }
        val currentDate = parseDate(currentItem?.timestamp ?: "")
        val nextDate = parseDate(nextItem.timestamp)
        return currentDate != nextDate
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun parseDate(timestamp: String): LocalDateTime {
        val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val parsedDate = dateTimeFormat.parse(timestamp)

        val calendar = Calendar.getInstance()
        calendar.time = parsedDate

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        return LocalDateTime.of(year, month, day, 0, 0)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun formatDateSeparator(timestamp: String): String {
        val parsedDate = parseDate(timestamp)
        val formattedDate = parsedDate.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일"))
        return formattedDate
    }

    private fun formatChatTimestamp(timestamp: String): String {
        val chatTimeFormat = SimpleDateFormat("a hh:mm", Locale.getDefault())
        val parsedTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(timestamp)
        return chatTimeFormat.format(parsedTime)
    }
}