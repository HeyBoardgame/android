package com.project.heyboardgame.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.heyboardgame.R
import com.project.heyboardgame.dataModel.Friend
import timber.log.Timber

class FriendListRVAdapter : PagingDataAdapter<Friend, FriendListRVAdapter.ViewHolder>(FriendListComparator) {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image = view.findViewById<ImageView>(R.id.profileImg)
        val nickname = view.findViewById<TextView>(R.id.nickname)
        val chatBtn = view.findViewById<ImageButton>(R.id.chatBtn)
    }

    object FriendListComparator : DiffUtil.ItemCallback<Friend>() {
        override fun areItemsTheSame(oldItem: Friend, newItem: Friend): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Friend, newItem: Friend): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_friend_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        item?.let {
            if (item.image != null) {
                Glide.with(holder.itemView.context).load(it.image).into(holder.image)
            } else {
                holder.image.setImageResource(R.drawable.default_profile_img)
            }
            holder.nickname.text = it.nickname

            holder.chatBtn.setOnClickListener {
                // 클릭 이벤트 처리
                Timber.d("채팅화면으로 이동")
            }
        }
    }
}