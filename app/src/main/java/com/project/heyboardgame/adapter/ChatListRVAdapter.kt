package com.project.heyboardgame.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.heyboardgame.R

class ChatListRVAdapter(val context : Context, val nicknameList : MutableList<String>)
    : RecyclerView.Adapter<ChatListRVAdapter.ViewHolder>() {

    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListRVAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_list, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatListRVAdapter.ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return nicknameList.size
    }
}