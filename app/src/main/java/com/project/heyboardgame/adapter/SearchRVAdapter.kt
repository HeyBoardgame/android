package com.project.heyboardgame.adapter


import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.heyboardgame.R
import com.project.heyboardgame.dataModel.SearchResultData

class SearchRVAdapter(val context : Context, var searchResList  : List<SearchResultData>)
    : RecyclerView.Adapter<SearchRVAdapter.ViewHolder>() {

    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {

        val gameImg = view.findViewById<ImageView>(R.id.gameImg)
        val title = view.findViewById<TextView>(R.id.title)
        val genre = view.findViewById<TextView>(R.id.genre)
        val difficulty = view.findViewById<TextView>(R.id.difficulty)
        val numOfPlayer = view.findViewById<TextView>(R.id.numOfPlayer)

    }

    @SuppressLint("NotifyDataSetChanged")
    fun setNewData(newData: List<SearchResultData>) {
        searchResList = newData
        notifyDataSetChanged()
    }

    interface ItemClick {
        fun onClick(view : View, position : Int)
    }
    var itemClick : ItemClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchRVAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search_res, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchRVAdapter.ViewHolder, position: Int) {
        val item = searchResList[position]

        Glide.with(context).load(item.image).into(holder.gameImg)
        holder.title.text = item.title
        holder.genre.text = item.genre.toCommaSeparatedString()
        holder.difficulty.text = item.difficulty
        holder.numOfPlayer.text = item.numOfPlayer

        if (itemClick != null) {
            holder.itemView.setOnClickListener { v ->
                itemClick?.onClick(v, position)
            }
        }

    }

    override fun getItemCount(): Int {
        return searchResList.size
    }

    private fun List<String>.toCommaSeparatedString(): String {
        return joinToString(", ")
    }

}