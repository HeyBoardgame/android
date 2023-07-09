package com.project.heyboardgame.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.heyboardgame.R

class FriendListRVAdapter(val context : Context, val nicknameList: MutableList<String>)
    : RecyclerView.Adapter<FriendListRVAdapter.ViewHolder>(){

    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendListRVAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.friend_list_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendListRVAdapter.ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return nicknameList.size
    }
}