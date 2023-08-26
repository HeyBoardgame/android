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
import com.project.heyboardgame.utils.ViewUtils
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

        friendRequestRVAdapter.addLoadStateListener { loadStates ->
            val noContentView = binding.noContent
            ViewUtils.setNoContentListener(loadStates, noContentView, friendRequestRVAdapter.itemCount)
        }

    }

    private fun loadFriendRequestPagingData() {
        mainViewModel.loadFriendRequestPagingData()

        mainViewModel.friendRequestPagingData.observe(viewLifecycleOwner) { pagingDataFlow ->
            viewLifecycleOwner.lifecycleScope.launch {
                pagingDataFlow.collectLatest { pagingData ->
                    friendRequestRVAdapter.submitData(pagingData)
                }
            }
        }
    }

    override fun onAcceptButtonClicked(friend: Friend) {
        mainViewModel.acceptFriendRequest(friend.id,
            onSuccess = {
                Toast.makeText(requireContext(), "${friend.nickname}님의 친구 요청을 수락했습니다.", Toast.LENGTH_SHORT).show()
                loadFriendRequestPagingData()
            },
            onFailure = {
                Toast.makeText(requireContext(), "친구 요청 수락에 실패했습니다.", Toast.LENGTH_SHORT).show()
            },
            onErrorAction = {
                Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        )
    }

    override fun onDeclineButtonClicked(friend: Friend) {
        mainViewModel.declineFriendRequest(friend.id,
            onSuccess = {
                Toast.makeText(requireContext(), "${friend.nickname}님의 친구 요청을 거절했습니다.", Toast.LENGTH_SHORT).show()
                loadFriendRequestPagingData()
            },
            onFailure = {
                Toast.makeText(requireContext(), "친구 요청 거절에 실패했습니다.", Toast.LENGTH_SHORT).show()
            },
            onErrorAction = {
                Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}