package com.project.heyboardgame.adapter

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.heyboardgame.R
import com.project.heyboardgame.dataModel.Timeline
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TimelineRVAdapter : PagingDataAdapter<Timeline, TimelineRVAdapter.ViewHolder>(TimelineComparator) {
    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val date = view.findViewById<TextView>(R.id.date)
        val time = view.findViewById<TextView>(R.id.time)
        val fourPlus = view.findViewById<ImageView>(R.id.fourPlus)
        val profileImgRV = view.findViewById<RecyclerView>(R.id.profileImgRV)
    }

    interface OnItemClickListener {
        fun onItemClick(item: Timeline)
    }

    private var itemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    object TimelineComparator : DiffUtil.ItemCallback<Timeline>() {
        override fun areItemsTheSame(oldItem: Timeline, newItem: Timeline): Boolean {
            return oldItem.timelineId == newItem.timelineId
        }

        override fun areContentsTheSame(oldItem: Timeline, newItem: Timeline): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineRVAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_timeline, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: TimelineRVAdapter.ViewHolder, position: Int) {
        val item = getItem(position)
        val previousItem = if (position - 1 >= 0) getItem(position - 1) else null

        item?.let {
            if (shouldShowDateSeparator(previousItem, item)) {
                holder.date.visibility = View.VISIBLE
                holder.date.text = formatDate(it.recommendedAt)
            } else {
                holder.date.visibility = View.GONE
            }
            holder.time.text = formatTimelineTimestamp(it.recommendedAt)
            if (it.isMemberMoreThanLimit) {
                holder.fourPlus.visibility = View.VISIBLE
            } else {
                holder.fourPlus.visibility = View.GONE
            }

            val profileImgAdapter = ProfileImgRVAdapter(it.memberProfileImages)
            holder.profileImgRV.layoutManager = LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
            holder.profileImgRV.adapter = profileImgAdapter

            holder.itemView.setOnClickListener {
                itemClickListener?.onItemClick(item)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun shouldShowDateSeparator(previousItem: Timeline?, currentItem: Timeline): Boolean {
        if (previousItem == null) {
            return true
        }
        val previousDate = parseDate(previousItem.recommendedAt)
        val currentDate = parseDate(currentItem.recommendedAt)
        return previousDate != currentDate
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun parseDate(timestamp: String): LocalDateTime {
        val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val parsedDate = dateTimeFormat.parse(timestamp)

        val calendar = Calendar.getInstance()
        calendar.time = parsedDate

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        return LocalDateTime.of(year, month, day, 0, 0)
    }

    private fun formatDate(timestamp: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val currentDate = Calendar.getInstance().time
        val parsedDate = dateFormat.parse(timestamp)
        val currentCalendar = Calendar.getInstance()
        currentCalendar.time = currentDate
        val parsedCalendar = Calendar.getInstance()
        parsedCalendar.time = parsedDate

        return when {
            isSameDay(currentDate, parsedDate) -> {
                "오늘"
            }
            isYesterday(currentDate, parsedDate) -> {
                "어제"
            }
            else -> {
                val yearFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
                yearFormat.format(parsedDate)
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun isSameDay(date1: Date, date2: Date): Boolean {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(date1) == dateFormat.format(date2)
    }

    @SuppressLint("SimpleDateFormat")
    private fun isYesterday(date1: Date, date2: Date): Boolean {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal1 = Calendar.getInstance()
        val cal2 = Calendar.getInstance()
        cal1.time = date1
        cal2.time = date2
        cal1.add(Calendar.DAY_OF_YEAR, -1)
        return dateFormat.format(cal1.time) == dateFormat.format(cal2.time)
    }

    private fun formatTimelineTimestamp(timestamp: String): String {
        val timelineTimeFormat = SimpleDateFormat("a hh:mm", Locale.getDefault())
        val parsedTime = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).parse(timestamp)
        return timelineTimeFormat.format(parsedTime)
    }
}