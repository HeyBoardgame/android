package com.project.heyboardgame.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.heyboardgame.R
import com.project.heyboardgame.dataModel.Friend
import com.project.heyboardgame.main.MainViewModel
import com.project.heyboardgame.utils.InterfaceUtils

class FriendRequestRVAdapter(private val interfaceUtils: InterfaceUtils)
    : PagingDataAdapter<Friend, FriendRequestRVAdapter.ViewHolder>(FriendRequestComparator) {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image = view.findViewById<ImageView>(R.id.profileImg)
        val nickname = view.findViewById<TextView>(R.id.nickname)
        val acceptBtn = view.findViewById<ImageButton>(R.id.acceptBtn)
        val declineBtn = view.findViewById<ImageButton>(R.id.declineBtn)
    }

    object FriendRequestComparator : DiffUtil.ItemCallback<Friend>() {
        override fun areItemsTheSame(oldItem: Friend, newItem: Friend): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Friend, newItem: Friend): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_friend_request, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        item?.let {
            Glide.with(holder.itemView.context).load(it.image).into(holder.image)
            holder.nickname.text = it.nickname

            holder.acceptBtn.setOnClickListener {
                interfaceUtils.onAcceptButtonClicked(item)
            }
            holder.declineBtn.setOnClickListener {
                interfaceUtils.onDeclineButtonClicked(item)
            }

        }
    }
}