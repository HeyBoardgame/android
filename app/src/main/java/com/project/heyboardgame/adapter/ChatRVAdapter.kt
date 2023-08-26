package com.project.heyboardgame.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.heyboardgame.R
import com.project.heyboardgame.dataModel.Chat

class ChatRVAdapter : PagingDataAdapter<Chat, RecyclerView.ViewHolder>(ChatComparator) {

    object ChatComparator : DiffUtil.ItemCallback<Chat>() {
        override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem == newItem
        }
    }

    companion object {
        private const val VIEW_TYPE_MY_MESSAGE = 1
        private const val VIEW_TYPE_FRIEND_MESSAGE = 2
    }

    inner class MyMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val myMessage = view.findViewById<TextView>(R.id.myMessage)
        val myMessageTime = view.findViewById<TextView>(R.id.myMessageTime)
    }

    inner class FriendMessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val friendProfileImg = view.findViewById<ImageView>(R.id.friendProfileImg)
        val friendNickname = view.findViewById<TextView>(R.id.friendNickname)
        val friendMessage = view.findViewById<TextView>(R.id.friendMessage)
        val friendMessageTime = view.findViewById<TextView>(R.id.friendMessageTime)
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)

        when (holder) {
            is MyMessageViewHolder -> {
                item?.let {
                    holder.myMessage.text = item.message
                    holder.myMessageTime.text = item.timestamp
                }
            }
            is FriendMessageViewHolder -> {
                item?.let {
                    if (item.image != null) {
                        Glide.with(holder.itemView.context).load(it.image).into(holder.friendProfileImg)
                    } else {
                        holder.friendProfileImg.setImageResource(R.drawable.default_profile_img)
                    }
                    holder.friendNickname.text = item.nickname
                    holder.friendMessage.text = item.message
                    holder.friendMessageTime.text = item.timestamp
                }
            }
        }
    }
}