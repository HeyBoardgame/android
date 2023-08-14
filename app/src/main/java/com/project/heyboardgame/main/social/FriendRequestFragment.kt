package com.project.heyboardgame.main.social


import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.heyboardgame.R
import com.project.heyboardgame.adapter.FriendRequestRVAdapter
import com.project.heyboardgame.dataModel.Friend
import com.project.heyboardgame.databinding.FragmentFriendRequestBinding
import com.project.heyboardgame.main.MainViewModel
import com.project.heyboardgame.utils.InterfaceUtils
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class FriendRequestFragment : Fragment(R.layout.fragment_friend_request), InterfaceUtils {
    // View Binding
    private var _binding : FragmentFriendRequestBinding? = null
    private val binding get() = _binding!!
    // View Model
    private lateinit var mainViewModel: MainViewModel
    // Adapter
    private lateinit var friendRequestRVAdapter : FriendRequestRVAdapter

    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFriendRequestBinding.bind(view)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        friendRequestRVAdapter = FriendRequestRVAdapter(this)
        binding.friendRequestRV.adapter = friendRequestRVAdapter
        binding.friendRequestRV.layoutManager = LinearLayoutManager(requireContext())

        loadFriendRequestPagingData()

    }

    private fun loadFriendRequestPagingData() {
        mainViewModel.loadFriendRequestPagingData()

        mainViewModel.friendRequestPagingData.observe(viewLifecycleOwner) { pagingDataFlow ->
            viewLifecycleOwner.lifecycleScope.launch {
                pagingDataFlow.collectLatest { pagingData ->
                    friendRequestRVAdapter.submitData(PagingData.empty())
                    friendRequestRVAdapter.submitData(pagingData)
                }
            }
        }
    }

    override fun onAcceptButtonClicked(friend: Friend) {
        Toast.makeText(requireContext(), "${friend.id} 수락", Toast.LENGTH_SHORT).show()
    }

    override fun onDeclineButtonClicked(friend: Friend) {
        Toast.makeText(requireContext(), "${friend.id} 거절", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}