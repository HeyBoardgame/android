package com.project.heyboardgame.main.social

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.heyboardgame.R
import com.project.heyboardgame.adapter.FriendListRVAdapter
import com.project.heyboardgame.adapter.TopRequestRVAdapter
import com.project.heyboardgame.dataModel.Friend
import com.project.heyboardgame.databinding.FragmentSocialBinding
import com.project.heyboardgame.main.MainViewModel
import com.project.heyboardgame.utils.InterfaceUtils
import com.project.heyboardgame.utils.ViewUtils
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class SocialFragment : Fragment(), InterfaceUtils {
    // 뒤로 가기 이벤트를 위한 변수
    private lateinit var callback : OnBackPressedCallback
    private var backPressedTime : Long = 0
    // View Binding
    private var _binding : FragmentSocialBinding? = null
    private val binding get() = _binding!!
    // View Model
    private lateinit var mainViewModel: MainViewModel
    // Adapter
    private lateinit var friendListRVAdapter : FriendListRVAdapter
    private lateinit var topRequestRVAdapter: TopRequestRVAdapter

    // 화면에서 뒤로 가기를 두 번 눌렀을 때 종료시켜주는 함수
    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (System.currentTimeMillis() - backPressedTime < 2500) {
                    activity?.finish()
                    return
                }
                Toast.makeText(activity, "한 번 더 누르면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show()
                backPressedTime = System.currentTimeMillis()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSocialBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        topRequestRVAdapter = TopRequestRVAdapter(emptyList(), this)
        binding.friendRequestRV.adapter = topRequestRVAdapter
        binding.friendRequestRV.layoutManager = LinearLayoutManager(requireContext())

        friendListRVAdapter = FriendListRVAdapter()
        binding.friendListRV.adapter = friendListRVAdapter
        binding.friendListRV.layoutManager = LinearLayoutManager(requireContext())

        getFriendRequestList()
        loadFriendListPagingData()

        binding.addFriendBtn.setOnClickListener {
            findNavController().navigate(R.id.action_socialFragment_to_addFriendFragment)
        }

        binding.chatListBtn.setOnClickListener {
            findNavController().navigate(R.id.action_socialFragment_to_chatListFragment)
        }

        binding.moreBtn.setOnClickListener {
            findNavController().navigate(R.id.action_socialFragment_to_friendRequestFragment)
        }

        friendListRVAdapter.addLoadStateListener { loadStates ->
            val noContentView = binding.noFriend
            ViewUtils.setNoContentListener(loadStates, noContentView, friendListRVAdapter.itemCount)
        }
    }

    private fun loadFriendListPagingData() {
        mainViewModel.loadFriendListPagingData()

        mainViewModel.friendListPagingData.observe(viewLifecycleOwner) { pagingDataFlow ->
            viewLifecycleOwner.lifecycleScope.launch {
                pagingDataFlow.collectLatest { pagingData ->
                    friendListRVAdapter.submitData(pagingData)
                }
            }
        }
    }

    private fun getFriendRequestList() {
        mainViewModel.getFriendRequestList(
            onSuccess = {
                if (it.isEmpty()) {
                    binding.friendRequest.visibility = View.GONE
                } else {
                    binding.friendRequest.visibility = View.VISIBLE
                    topRequestRVAdapter.updateData(it)
                }
            },
            onFailure = {
                Toast.makeText(requireContext(), "친구 요청 목록 조회에 실패했습니다.", Toast.LENGTH_SHORT).show()
            },
            onErrorAction = {
                Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        )
    }

    override fun onAcceptButtonClicked(friend: Friend) {
        mainViewModel.acceptFriendRequest(friend.id,
            onSuccess = {
                Toast.makeText(requireContext(), "${friend.nickname}님의 친구 요청을 수락했습니다.", Toast.LENGTH_SHORT).show()
                getFriendRequestList()
                loadFriendListPagingData()
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
                getFriendRequestList()
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

    // 뒤로 가기 두 번을 위해 추가
    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }
}