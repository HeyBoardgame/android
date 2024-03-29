package com.project.heyboardgame.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.project.heyboardgame.R
import com.project.heyboardgame.dataModel.Friend
import com.project.heyboardgame.main.MainViewModel
import com.project.heyboardgame.main.social.SocialFragmentDirections
import com.project.heyboardgame.utils.GlideUtils
import timber.log.Timber

class FriendListRVAdapter(private val mainViewModel: MainViewModel) : PagingDataAdapter<Friend, FriendListRVAdapter.ViewHolder>(FriendListComparator) {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image = view.findViewById<ImageView>(R.id.profileImg)
        val nickname = view.findViewById<TextView>(R.id.nickname)
        val chatBtn = view.findViewById<ImageButton>(R.id.chatBtn)
    }

    object FriendListComparator : DiffUtil.ItemCallback<Friend>() {
        override fun areItemsTheSame(oldItem: Friend, newItem: Friend): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Friend, newItem: Friend): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_friend_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        item?.let {
            GlideUtils.loadThumbnailImage(holder.itemView.context, it.image, holder.image)
            holder.nickname.text = it.nickname

            holder.chatBtn.setOnClickListener {
                mainViewModel.requestRoomId(item.id,
                    onSuccess = { roomId ->
                        val navController = Navigation.findNavController(holder.itemView)
                        val action = SocialFragmentDirections.actionSocialFragmentToChatFragment(item, roomId)
                        navController.navigate(action)
                    },
                    onFailure = {
                        Timber.d("채팅방 아이디 불러오기 실패")
                    },
                    onErrorAction = {
                        Timber.d("네트워크 오류")
                    }
                )
            }
        }
    }
}