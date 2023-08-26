package com.project.heyboardgame.main.social


import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.heyboardgame.R
import com.project.heyboardgame.adapter.ChatRVAdapter
import com.project.heyboardgame.dataModel.Chat
import com.project.heyboardgame.databinding.FragmentChatBinding
import com.project.heyboardgame.main.MainViewModel
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import kotlin.properties.Delegates


class ChatFragment : Fragment(R.layout.fragment_chat) {
    // View Binding
    private var _binding : FragmentChatBinding? = null
    private val binding get() = _binding!!
    // View Model
    private lateinit var mainViewModel: MainViewModel
    // Adapter
    private lateinit var chatRVAdapter: ChatRVAdapter
    // 친구 아이디
    private var friendId = 0
    // 소켓
    private lateinit var socket: Socket


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        _binding = FragmentChatBinding.bind(view)

        val args : ChatFragmentArgs by navArgs()
        friendId = args.id
        val nickname = args.nickname

        val options = IO.Options()
        options.query = "friendId=$friendId" // friendId를 쿼리 파라미터로 전송
        socket = IO.socket("http://13.125.211.203:8080/api/v1/chats/$friendId", options)
        socket.connect()

        binding.friendNickname.text = nickname

        chatRVAdapter = ChatRVAdapter()
        binding.chatRV.adapter = chatRVAdapter
        binding.chatRV.layoutManager = LinearLayoutManager(requireContext()).apply {
            reverseLayout = true
            stackFromEnd = true
        }
        loadChatPagingData(friendId)

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.sendBtn.setOnClickListener {
            val message = binding.chatEditText.text.toString()
            if (message.isNotEmpty()) {
                sendMessage(message) // 메시지 전송 함수 호출
                binding.chatEditText.text.clear() // 메시지 입력창 비우기
            }
        }
        socket.on(Socket.EVENT_CONNECT) {
            Timber.d("Socket Connected")
        }

        socket.on(Socket.EVENT_DISCONNECT) {
            Timber.d("Socket Disconnected")
        }

        socket.on("receive message") { args ->
            val message = args[0] as JSONObject
            val chat = Chat(
                id = message.getInt("id"),
                image = message.getString("imagePath"),
                nickname = message.getString("nickname"),
                message = message.getString("message"),
                timestamp = message.getString("timestamp"),
                isMyMessage = false
            )
        }
    }

    private fun loadChatPagingData(id: Int) {
        mainViewModel.loadChatPagingData(id)

        mainViewModel.chatPagingData.observe(viewLifecycleOwner) { pagingDataFlow ->
            viewLifecycleOwner.lifecycleScope.launch {
                pagingDataFlow.collectLatest { pagingData ->
                    chatRVAdapter.submitData(pagingData)
                }
            }
        }
    }

    private fun sendMessage(message: String) {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("message", message)
            socket.emit("send message", jsonObject)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        socket.disconnect()
    }
}