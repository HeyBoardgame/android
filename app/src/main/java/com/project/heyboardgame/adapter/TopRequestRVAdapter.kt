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
import com.project.heyboardgame.utils.InterfaceUtils

class TopRequestRVAdapter(var friendRequestList : List<Friend>, private val interfaceUtils: InterfaceUtils)
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

        Glide.with(holder.itemView.context).load(item.image).into(holder.image)
        holder.nickname.text = item.nickname
        holder.acceptBtn.setOnClickListener {
            interfaceUtils.onAcceptButtonClicked(item)
        }
        holder.declineBtn.setOnClickListener {
            interfaceUtils.onDeclineButtonClicked(item)
        }
    }

    override fun getItemCount(): Int {
        return friendRequestList.size
    }

}