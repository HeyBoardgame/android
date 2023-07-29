package com.project.heyboardgame.adapter


import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.heyboardgame.R
import com.project.heyboardgame.dataModel.SearchResultData
import java.util.Locale

class SearchRVAdapter(val context : Context, val searchResList  : List<SearchResultData>)
    : RecyclerView.Adapter<SearchRVAdapter.ViewHolder>() {


    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {

        val boardGameImg = view.findViewById<ImageView>(R.id.gameImg)
        val boardGameTitle = view.findViewById<TextView>(R.id.title)
        val boardGameGenre = view.findViewById<TextView>(R.id.genre)
        val boardGameDifficulty = view.findViewById<TextView>(R.id.difficulty)
        val boardGameNumOfPeople = view.findViewById<TextView>(R.id.numOfPeople)

    }

    interface ItemClick {
        fun onClick(view : View, position : Int)
    }
    var itemClick : ItemClick? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchRVAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_res_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchRVAdapter.ViewHolder, position: Int) {
        val item = searchResList[position]

        holder.boardGameTitle.text = item.boardGameTitle
        holder.boardGameGenre.text = item.boardGameGenre.toCommaSeparatedString()
        holder.boardGameDifficulty.text = item.boardGameDifficulty
        holder.boardGameNumOfPeople.text = item.boardGameNumOfPeople

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