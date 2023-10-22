package com.project.heyboardgame.main.social


import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.project.heyboardgame.R
import com.project.heyboardgame.adapter.ChatRVAdapter
import com.project.heyboardgame.dataModel.Chat
import com.project.heyboardgame.dataModel.ChatData
import com.project.heyboardgame.dataModel.Friend
import com.project.heyboardgame.dataStore.MyDataStore
import com.project.heyboardgame.databinding.FragmentChatBinding
import com.project.heyboardgame.main.MainViewModel
import com.project.heyboardgame.websocket.MyWebSocketListener
import com.project.heyboardgame.websocket.WebSocketCallback
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Locale


class ChatFragment : Fragment(R.layout.fragment_chat), WebSocketCallback {
    // View Binding
    private var _binding : FragmentChatBinding? = null
    private val binding get() = _binding!!
    // View Model
    private lateinit var mainViewModel: MainViewModel
    // DataStore
    private val myDataStore : MyDataStore = MyDataStore()
    // Adapter
    private lateinit var chatRVAdapter: ChatRVAdapter
    // 소켓
    private lateinit var webSocket: WebSocket
    // 친구 정보
    private lateinit var friendInfo: Friend
    private var isInitialLoad: Boolean = true

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        _binding = FragmentChatBinding.bind(view)

        val args : ChatFragmentArgs by navArgs()
        friendInfo = args.friend

        val accessToken = runBlocking { myDataStore.getAccessToken() }

        binding.friendNickname.text = friendInfo.nickname

        chatRVAdapter = ChatRVAdapter(friendInfo)
        binding.chatRV.adapter = chatRVAdapter
        binding.chatRV.layoutManager = LinearLayoutManager(requireContext()).apply {
            reverseLayout = true
            stackFromEnd = true
        }

        binding.chatRV.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (isInitialLoad) {
                binding.chatRV.scrollToPosition(0)
                isInitialLoad = false
            } else if (bottom < oldBottom) {
                binding.chatRV.scrollBy(0, oldBottom - bottom)
            } else if (bottom > oldBottom) {
                val layoutManager = binding.chatRV.layoutManager as LinearLayoutManager
                val firstVisibleItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition()
                if (firstVisibleItemPosition != 0) {
                    binding.chatRV.scrollBy(0, oldBottom - bottom)
                }
            }
        }

        val webSocketUrl = "ws://13.125.211.203:8080/api/v1/chats/send-message/${friendInfo.id}"
        val webSocketListener = MyWebSocketListener(this)
        val request = Request.Builder()
            .url(webSocketUrl)
            .header("Authorization", "Bearer $accessToken") // 여기에 액세스 토큰을 직접 추가
            .build()
        val okHttpClient = OkHttpClient()
        webSocket = okHttpClient.newWebSocket(request, webSocketListener)

        loadChatPagingData(friendInfo.id)

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.sendBtn.setOnClickListener {
            val message = binding.chatEditText.text.toString()
            if (message.isNotEmpty()) {
                sendMessage(message)
            }
        }

        binding.chatRV.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                // 클릭 이벤트가 발생하면 키보드를 숨기는 함수 호출
                hideKeyboard()
                binding.chatEditText.clearFocus()
            }
            return@setOnTouchListener false
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val focusedView = requireActivity().currentFocus
        if (focusedView != null) {
            inputMethodManager.hideSoftInputFromWindow(focusedView.windowToken, 0)
        }
    }

    private fun scrollToPositionOnMainThread() {
        binding.chatRV.post {
            binding.chatRV.smoothScrollToPosition(0)
        }
    }

    private fun sendMessage(message: String) {
        val currentTime = System.currentTimeMillis()
        val dataFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
        val formattedTime = dataFormat.format(currentTime)

        val chatMessage = ChatData(message = message, timestamp = formattedTime)
        val jsonMessage = Gson().toJson(chatMessage)
        if (webSocket.send(jsonMessage)) {

            val newChat = Chat(id = 0, message = message, timestamp = formattedTime, isMyMessage = true)

            // 현재 데이터를 가져옴
            val currentData = chatRVAdapter.snapshot().items
            // 새 아이템을 추가한 데이터 리스트를 만듦
            val newData = listOf(newChat) + currentData
            // 새 데이터를 어댑터에 제출
            chatRVAdapter.submitData(lifecycle, PagingData.from(newData))

            binding.chatEditText.text.clear() // 메시지 입력창 비우기
            scrollToPositionOnMainThread()
        } else {
            // 메시지 전송 실패 처리
            Timber.e("Failed to send message")
        }
    }

    override fun onMessageReceived(senderId: Int, message: String, timestamp: String) {
        if (senderId == friendInfo.id) {

            val newChat = Chat(id = 0, message = message, timestamp = timestamp, isMyMessage = false)

            val currentData = chatRVAdapter.snapshot().items
            val newData = listOf(newChat) + currentData
            chatRVAdapter.submitData(lifecycle, PagingData.from(newData))
            scrollToPositionOnMainThread()
            chatRVAdapter.notifyItemRangeInserted(0, chatRVAdapter.itemCount + 1)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        webSocket.close(1000, "Fragment destroyed")
    }
}