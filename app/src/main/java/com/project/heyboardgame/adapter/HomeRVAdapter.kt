package com.project.heyboardgame.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.heyboardgame.R
import com.project.heyboardgame.dataModel.BoardGame

class HomeRVAdapter(var boardgameList : List<BoardGame>) : RecyclerView.Adapter<HomeRVAdapter.ViewHolder>() {

    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val image = view.findViewById<ImageView>(R.id.image)
        val title = view.findViewById<TextView>(R.id.title)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newData: List<BoardGame>) {
        boardgameList = newData
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(item: BoardGame)
    }

    private var itemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeRVAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_boardgame, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeRVAdapter.ViewHolder, position: Int) {
        val item = boardgameList[position]

        Glide.with(holder.itemView.context).load(item.image).into(holder.image)
        holder.title.text = item.title
        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(item)
        }
    }

    override fun getItemCount(): Int {
        return boardgameList.size
    }

}