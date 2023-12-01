package com.project.heyboardgame.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.project.heyboardgame.R
import com.project.heyboardgame.utils.GlideUtils

class ProfileImgRVAdapter(var profileImgList: List<String?>) : RecyclerView.Adapter<ProfileImgRVAdapter.ViewHolder>()  {
    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val profileImg = view.findViewById<ImageView>(R.id.friendProfileImg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileImgRVAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_profile_img, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProfileImgRVAdapter.ViewHolder, position: Int) {
        val item = profileImgList[position]

        GlideUtils.loadThumbnailImage(holder.itemView.context, item, holder.profileImg)
    }

    override fun getItemCount(): Int {
        return profileImgList.size
    }


}