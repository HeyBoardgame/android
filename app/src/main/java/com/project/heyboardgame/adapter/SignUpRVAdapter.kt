package com.project.heyboardgame.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.heyboardgame.R

class SignUpRVAdapter(val context : Context, val genreList : MutableList<String>)
    : RecyclerView.Adapter<SignUpRVAdapter.ViewHolder>(){

    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SignUpRVAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sign_up_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: SignUpRVAdapter.ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return genreList.size
    }
}