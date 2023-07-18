package com.project.heyboardgame.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.heyboardgame.R
import com.project.heyboardgame.dataModel.GroupMatchResult

class MatchResRVAdapter(val context : Context, val matchResList : List<GroupMatchResult>)
    : RecyclerView.Adapter<MatchResRVAdapter.ViewHolder>() {

    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchResRVAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.match_res_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: MatchResRVAdapter.ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return matchResList.size
    }
}