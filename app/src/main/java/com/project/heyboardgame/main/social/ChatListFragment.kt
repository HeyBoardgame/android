package com.project.heyboardgame.main.social

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.heyboardgame.R
import com.project.heyboardgame.adapter.ChatListRVAdapter
import com.project.heyboardgame.databinding.FragmentChatListBinding


class ChatListFragment : Fragment(R.layout.fragment_chat_list) {

    // View Binding
    private var _binding : FragmentChatListBinding? = null
    private val binding get() = _binding!!
    // Adapter
    private lateinit var chatListRVAdapter: ChatListRVAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentChatListBinding.bind(view)


        binding.addFriendBtn.setOnClickListener {
            findNavController().navigate(R.id.action_chatListFragment_to_addFriendFragment)
        }

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }


        var nicknameList = mutableListOf<String>()
        nicknameList.add("닉네임1")
        nicknameList.add("닉네임2")
        nicknameList.add("닉네임3")
        nicknameList.add("닉네임4")
        nicknameList.add("닉네임5")

        chatListRVAdapter = ChatListRVAdapter(requireContext(), nicknameList)
        binding.chatListRV.adapter = chatListRVAdapter
        binding.chatListRV.layoutManager = LinearLayoutManager(requireContext())

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}