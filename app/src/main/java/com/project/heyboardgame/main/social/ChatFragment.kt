package com.project.heyboardgame.main.social


import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    // 채팅
    private var currentChatList: MutableList<Chat> = mutableListOf()
    private var nextPage: Int? = 0
    private var isRequested: Boolean = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        _binding = FragmentChatBinding.bind(view)

        val args : ChatFragmentArgs by navArgs()
        friendInfo = args.friend

        val accessToken = runBlocking { myDataStore.getAccessToken() }

        binding.friendNickname.text = friendInfo.nickname

        chatRVAdapter = ChatRVAdapter(mutableListOf(), friendInfo)
        binding.chatRV.adapter = chatRVAdapter
        binding.chatRV.layoutManager = LinearLayoutManager(requireContext()).apply {
            reverseLayout = true
            stackFromEnd = true
        }

        mainViewModel.getChatting(friendInfo.id, 0, 15,
            onSuccess = {
                currentChatList.addAll(it.content)
                chatRVAdapter.addAll(it.content as MutableList<Chat>)
                binding.chatRV.scrollToPosition(0)
                nextPage = it.nextPage
            },
            onFailure = {
                Toast.makeText(requireContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            },
            onErrorAction = {
                Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        )

        binding.chatRV.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (bottom < oldBottom) {
                binding.chatRV.scrollBy(0, oldBottom - bottom)
            } else if (bottom > oldBottom) {
                val layoutManager = binding.chatRV.layoutManager as LinearLayoutManager
                val firstVisibleItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition()
                if (firstVisibleItemPosition != 0) {
                    binding.chatRV.scrollBy(0, oldBottom - bottom)
                }
            }
        }

        binding.chatRV.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = binding.chatRV.layoutManager as LinearLayoutManager
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                val totalCount = recyclerView.adapter?.itemCount?.minus(1)

                if (lastVisibleItem == totalCount && !isRequested) {
                    isRequested = true
                    loadNextPage(nextPage)
                }
            }
        })

        val webSocketUrl = "ws://13.125.211.203:8080/api/v1/chats/send-message/${friendInfo.id}"
        val webSocketListener = MyWebSocketListener(this)
        val request = Request.Builder()
            .url(webSocketUrl)
            .header("Authorization", "Bearer $accessToken")
            .build()
        val okHttpClient = OkHttpClient()
        webSocket = okHttpClient.newWebSocket(request, webSocketListener)

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

    private fun loadNextPage(pageNum: Int?) {
        if (pageNum != null) {
            mainViewModel.getChatting(friendInfo.id, pageNum, 15,
                onSuccess = {
                    currentChatList.addAll(it.content)
                    chatRVAdapter.addAll(it.content as MutableList<Chat>)
                    nextPage = it.nextPage
                    isRequested = false
                },
                onFailure = {
                    Toast.makeText(requireContext(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                },
                onErrorAction = {
                    Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            )
        } else {
            return
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
            activity?.runOnUiThread {
                chatRVAdapter.add(newChat)
                scrollToPositionOnMainThread()
            }
            binding.chatEditText.text.clear()
        } else {
            // 메시지 전송 실패 처리
            Timber.e("Failed to send message")
        }
    }

    override fun onMessageReceived(senderId: Int, message: String, timestamp: String) {
        if (senderId == friendInfo.id) {
            val newChat = Chat(id = 0, message = message, timestamp = timestamp, isMyMessage = false)
            activity?.runOnUiThread {
                chatRVAdapter.add(newChat)
                scrollToPositionOnMainThread()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        webSocket.close(1000, "Fragment destroyed")
    }
}