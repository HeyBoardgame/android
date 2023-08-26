package com.project.heyboardgame.main.social

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.heyboardgame.R
import com.project.heyboardgame.adapter.ChatListRVAdapter
import com.project.heyboardgame.dataModel.ChatRoom
import com.project.heyboardgame.databinding.FragmentChatListBinding
import com.project.heyboardgame.main.MainViewModel
import com.project.heyboardgame.utils.ViewUtils
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class ChatListFragment : Fragment(R.layout.fragment_chat_list) {
    // View Binding
    private var _binding : FragmentChatListBinding? = null
    private val binding get() = _binding!!
    // Adapter
    private lateinit var chatListRVAdapter: ChatListRVAdapter
    // View Model
    private lateinit var mainViewModel: MainViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        _binding = FragmentChatListBinding.bind(view)

        chatListRVAdapter = ChatListRVAdapter()
        binding.chatListRV.adapter = chatListRVAdapter
        binding.chatListRV.layoutManager = LinearLayoutManager(requireContext())

        chatListRVAdapter.setOnItemClickListener(object : ChatListRVAdapter.OnItemClickListener {
            override fun onItemClick(item: ChatRoom) {
                val action = ChatListFragmentDirections.actionChatListFragmentToChatFragment(item.userInfo.id, item.userInfo.nickname)
                findNavController().navigate(action)
            }
        })

        chatListRVAdapter.addLoadStateListener { loadStates ->
            val noContentView = binding.noContent
            ViewUtils.setNoContentListener(loadStates, noContentView, chatListRVAdapter.itemCount)
        }

        loadChatListPagingData()

        binding.addFriendBtn.setOnClickListener {
            findNavController().navigate(R.id.action_chatListFragment_to_addFriendFragment)
        }

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun loadChatListPagingData() {
        mainViewModel.loadChatListPagingData()

        mainViewModel.chatListPagingData.observe(viewLifecycleOwner) { pagingDataFlow ->
            viewLifecycleOwner.lifecycleScope.launch {
                pagingDataFlow.collectLatest { pagingData ->
                    chatListRVAdapter.submitData(pagingData)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}