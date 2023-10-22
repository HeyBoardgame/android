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
import com.project.heyboardgame.dataModel.BoardGame2

class MatchResRVAdapter(var recommendations : List<BoardGame2>) : RecyclerView.Adapter<MatchResRVAdapter.ViewHolder>() {

    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val gameImg = view.findViewById<ImageView>(R.id.gameImg)
        val title = view.findViewById<TextView>(R.id.title)
        val genre = view.findViewById<TextView>(R.id.genre)
        val difficulty = view.findViewById<TextView>(R.id.difficulty)
        val numOfPlayer = view.findViewById<TextView>(R.id.numOfPlayer)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newData: List<BoardGame2>) {
        recommendations = newData
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(item: BoardGame2)
    }
    private var itemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchResRVAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_result, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: MatchResRVAdapter.ViewHolder, position: Int) {
        val item = recommendations[position]

        item.let {
            Glide.with(holder.itemView.context).load(it.image).into(holder.gameImg)
            holder.title.text = it.title
            holder.genre.text = it.genre.toCommaSeparatedString()
            holder.difficulty.text = it.difficulty
            holder.numOfPlayer.text = createPlayerString(it.playerMin, it.playerMax)

            holder.itemView.setOnClickListener {
                itemClickListener?.onItemClick(item)
            }
        }
    }

    private fun List<String>.toCommaSeparatedString(): String {
        return joinToString(", ")
    }

    private fun createPlayerString(min: Int, max: Int): String {
        return if (min == max) {
            "${min}인"
        } else {
            "${min}~${max}인"
        }
    }

    override fun getItemCount(): Int {
        return recommendations.size
    }
}