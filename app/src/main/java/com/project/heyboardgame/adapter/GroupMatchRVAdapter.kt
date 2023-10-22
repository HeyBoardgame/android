package com.project.heyboardgame.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.heyboardgame.R
import com.project.heyboardgame.dataModel.Friend

class GroupMatchRVAdapter(private val group : List<Friend>) : RecyclerView.Adapter<GroupMatchRVAdapter.ViewHolder>(){
    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val image = view.findViewById<ImageView>(R.id.profileImg)
        val nickname = view.findViewById<TextView>(R.id.nickname)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupMatchRVAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_group_match, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupMatchRVAdapter.ViewHolder, position: Int) {
        val item = group[position]

        if (item.image != null) {
            Glide.with(holder.itemView.context).load(item.image).into(holder.image)
        } else {
            holder.image.setImageResource(R.drawable.default_profile_img)
        }
        holder.nickname.text = item.nickname
    }

    override fun getItemCount(): Int {
        return group.size
    }
}