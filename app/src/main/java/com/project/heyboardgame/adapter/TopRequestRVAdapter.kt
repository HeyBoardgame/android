package com.project.heyboardgame.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.heyboardgame.R
import com.project.heyboardgame.dataModel.Friend
import com.project.heyboardgame.utils.FriendRequestCallback

class TopRequestRVAdapter(var friendRequestList : List<Friend>, private val friendRequestCallback: FriendRequestCallback)
    : RecyclerView.Adapter<TopRequestRVAdapter.ViewHolder>() {

    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val image = view.findViewById<ImageView>(R.id.profileImg)
        val nickname = view.findViewById<TextView>(R.id.nickname)
        val acceptBtn = view.findViewById<ImageButton>(R.id.acceptBtn)
        val declineBtn = view.findViewById<ImageButton>(R.id.declineBtn)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newData: List<Friend>) {
        friendRequestList = newData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopRequestRVAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_friend_request, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: TopRequestRVAdapter.ViewHolder, position: Int) {
        val item = friendRequestList[position]

        if (item.image != null) {
            Glide.with(holder.itemView.context).load(item.image).into(holder.image)
        } else {
            holder.image.setImageResource(R.drawable.default_profile_img)
        }
        holder.nickname.text = item.nickname
        holder.acceptBtn.setOnClickListener {
            friendRequestCallback.onAcceptButtonClicked(item)
        }
        holder.declineBtn.setOnClickListener {
            friendRequestCallback.onDeclineButtonClicked(item)
        }
    }

    override fun getItemCount(): Int {
        return friendRequestList.size
    }

}