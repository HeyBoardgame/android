package com.project.heyboardgame.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.heyboardgame.R

class FriendRequestRVAdapter(val context : Context, val nicknameList: MutableList<String>)
    : RecyclerView.Adapter<FriendRequestRVAdapter.ViewHolder>(){

    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendRequestRVAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.friend_request_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendRequestRVAdapter.ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return nicknameList.size
    }
}