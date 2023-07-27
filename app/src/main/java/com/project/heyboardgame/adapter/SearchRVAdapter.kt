package com.project.heyboardgame.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.heyboardgame.R
import com.project.heyboardgame.dataModel.SearchResultData

class SearchRVAdapter(val context : Context, val matchResList : List<SearchResultData>)
    : RecyclerView.Adapter<SearchRVAdapter.ViewHolder>() {

    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchRVAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_res_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchRVAdapter.ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return matchResList.size
    }

}