package com.project.heyboardgame.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.heyboardgame.R
import com.project.heyboardgame.retrofit.dataModel.BoardgameList

class HomeRVAdapter(val context : Context, val boardgameList : List<BoardgameList>)
    : RecyclerView.Adapter<HomeRVAdapter.ViewHolder>() {

    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeRVAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.boardgame_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeRVAdapter.ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return boardgameList.size
    }


}