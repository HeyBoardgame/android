package com.project.heyboardgame.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.heyboardgame.R
import com.project.heyboardgame.dataModel.SignUpItem

class SignUpRVAdapter(val context : Context, val genreList : List<SignUpItem>)
    : RecyclerView.Adapter<SignUpRVAdapter.ViewHolder>(){

    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {

        val genreItem = view.findViewById<LinearLayout>(R.id.genreItem)
        val genreIcon = view.findViewById<ImageView>(R.id.genreIcon)
        val genreName = view.findViewById<TextView>(R.id.genreName)
        val likeBtn = view.findViewById<ImageView>(R.id.likeBtn)

    }

    // 선택된 장르들의 ID를 반환하는 함수
    fun getSelectedItems(): List<String> {
        val selectedItems = mutableListOf<String>()
        for (item in genreList) {
            if (item.isClicked) {
                selectedItems.add(item.getItemId())
            }
        }
        return selectedItems
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SignUpRVAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sign_up_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: SignUpRVAdapter.ViewHolder, position: Int) {

        val item = genreList[position]
        holder.genreIcon.setImageResource(item.genreIcon)
        holder.genreName.text = item.genreName

        holder.genreItem.setOnClickListener {
            if (item.isClicked) { // 하트가 눌려있는 상태
                item.isClicked = false
                holder.likeBtn.setImageResource(R.drawable.icon_like_grey)
            } else { // 하트가 안 눌려있는 상태
                item.isClicked = true
                holder.likeBtn.setImageResource(R.drawable.icon_like_color)
            }
        }
    }

    override fun getItemCount(): Int {
        return genreList.size
    }
}