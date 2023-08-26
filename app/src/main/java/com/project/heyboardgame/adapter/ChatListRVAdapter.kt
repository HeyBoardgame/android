package com.project.heyboardgame.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.heyboardgame.R
import com.project.heyboardgame.dataModel.BoardGame
import com.project.heyboardgame.dataModel.ChatRoom
import com.project.heyboardgame.dataModel.Friend
import org.w3c.dom.Text
import timber.log.Timber

class ChatListRVAdapter : PagingDataAdapter<ChatRoom, ChatListRVAdapter.ViewHolder>(ChatListComparator) {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image = view.findViewById<ImageView>(R.id.profileImg)
        val nickname = view.findViewById<TextView>(R.id.nickname)
        val lastMessage = view.findViewById<TextView>(R.id.lastMessage)
        val timestamp = view.findViewById<TextView>(R.id.timestamp)
        val unread = view.findViewById<CardView>(R.id.unread)
        val unreadNum = view.findViewById<TextView>(R.id.unreadNum)
    }

    interface OnItemClickListener {
        fun onItemClick(item: ChatRoom)
    }

    private var itemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    object ChatListComparator : DiffUtil.ItemCallback<ChatRoom>() {
        override fun areItemsTheSame(oldItem: ChatRoom, newItem: ChatRoom): Boolean {
            return oldItem.roomId == newItem.roomId
        }

        override fun areContentsTheSame(oldItem: ChatRoom, newItem: ChatRoom): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        item?.let {
            if (item.userInfo.image != null) {
                Glide.with(holder.itemView.context).load(it.userInfo.image).into(holder.image)
            } else {
                holder.image.setImageResource(R.drawable.default_profile_img)
            }
            holder.nickname.text = it.userInfo.nickname
            holder.lastMessage.text = it.lastMessage
            holder.timestamp.text = it.timestamp
//            if (item.unreadMessage != null) {
//                holder.unread.visibility = View.VISIBLE
//                holder.unreadNum.text = it.unreadMessage.toString()
//            } else {
//                holder.unread.visibility = View.INVISIBLE
//            }
            holder.itemView.setOnClickListener {
                itemClickListener?.onItemClick(item)
            }
        }
    }
}