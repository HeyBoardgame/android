package com.project.heyboardgame.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.heyboardgame.R

class BadgeRVAdapter(val context : Context, val badgeList : MutableList<String>)
    : RecyclerView.Adapter<BadgeRVAdapter.ViewHolder>() {

    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeRVAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.badge_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: BadgeRVAdapter.ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return badgeList.size
    }

}