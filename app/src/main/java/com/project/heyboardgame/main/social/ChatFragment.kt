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
import com.project.heyboardgame.R
import com.project.heyboardgame.adapter.ChatRVAdapter
import com.project.heyboardgame.dataModel.Chat
import com.project.heyboardgame.dataModel.Friend
import com.project.heyboardgame.dataStore.MyDataStore
import com.project.heyboardgame.databinding.FragmentChatBinding
import com.project.heyboardgame.main.MainViewModel
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import timber.log.Timber
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompHeader
import java.text.SimpleDateFormat
import java.util.Locale


class ChatFragment : Fragment(R.layout.fragment_chat) {
    // View Binding
    private var _binding : FragmentChatBinding? = null
    private val binding get() = _binding!!
    // View Model
    private lateinit var mainViewModel: MainViewModel
    // DataStore
    private val myDataStore : MyDataStore = MyDataStore()
    // Adapter
    private lateinit var chatRVAdapter: ChatRVAdapter
    // 친구 정보
    private lateinit var friendInfo: Friend
    // 채팅
    private var currentChatList: MutableList<Chat> = mutableListOf()
    private var nextPage: Int? = 0
    private var isRequested: Boolean = false
    // 액세스 토큰
    private var accessToken: String = ""
    // 페이징 크기
    private val size = 30
    // STOMP
    private lateinit var stompClient : StompClient
    private lateinit var stompConnection: Disposable
    private lateinit var topic: Disposable
    private var isPaused = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        _binding = FragmentChatBinding.bind(view)

        val args : ChatFragmentArgs by navArgs()
        friendInfo = args.friend

        accessToken = runBlocking { myDataStore.getAccessToken() }

        binding.friendNickname.text = friendInfo.nickname

        chatRVAdapter = ChatRVAdapter(mutableListOf(), friendInfo)
        binding.chatRV.adapter = chatRVAdapter
        binding.chatRV.layoutManager = LinearLayoutManager(requireContext()).apply {
            reverseLayout = true
            stackFromEnd = true
        }

        mainViewModel.getChatting(friendInfo.id, 0, size,
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

        connectStomp()

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
            mainViewModel.getChatting(friendInfo.id, pageNum, size,
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

    private fun connectStomp() {
        val url = "ws://13.125.211.203:8080/api/v1/chats/connect/${friendInfo.id}"
        stompClient =  Stomp.over(Stomp.ConnectionProvider.OKHTTP, url)

        val headerList = arrayListOf<StompHeader>()
        headerList.add(StompHeader("Authorization", "Bearer $accessToken"))
        stompClient.connect(headerList)

        topic = stompClient.topic("/sub/send-message/${friendInfo.id}").subscribe { topicMessage ->
            Timber.d("${topicMessage.payload}")
            val jsonObject = JSONObject(topicMessage.payload)
            val message = jsonObject.getString("msg")
            Timber.d("${message}")

            receiveMessage(topicMessage.payload)
        }

        stompConnection = stompClient.lifecycle().subscribe { lifecycleEvent ->
            when (lifecycleEvent.type) {
                LifecycleEvent.Type.OPENED -> {
                    Timber.d("Open")
                }
                LifecycleEvent.Type.CLOSED -> {
                    Timber.d("Closed")

                }
                LifecycleEvent.Type.ERROR -> {
                    Timber.d("Error: ${lifecycleEvent.exception}")
                }
                else ->{
                    Timber.d("Else: ${lifecycleEvent.message}")
                }
            }
        }
    }

    private fun sendMessage(message: String) {
        val currentTime = System.currentTimeMillis()
        val dataFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
        val formattedTime = dataFormat.format(currentTime)

        val data = JSONObject()
        data.put("msg", message)
        data.put("timeStamp", formattedTime)
        data.put("accessToken", accessToken)

        stompClient.send("/pub/send-message/${friendInfo.id}", data.toString()).subscribe()

        val newChat = Chat(id = 0, message = message, timestamp = formattedTime, isMyMessage = true)
        activity?.runOnUiThread {
            chatRVAdapter.add(newChat)
            scrollToPositionOnMainThread()
        }
        binding.chatEditText.text.clear()
    }

    private fun receiveMessage(message: String) {
        val currentTime = System.currentTimeMillis()
        val dataFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
        val formattedTime = dataFormat.format(currentTime)

        val newChat = Chat(id = 0, message = message, timestamp = formattedTime, isMyMessage = false)
        activity?.runOnUiThread {
            chatRVAdapter.add(newChat)
            scrollToPositionOnMainThread()
        }
    }

    override fun onPause() {
        super.onPause()
        isPaused = true
        stompClient.disconnect()
        stompConnection.dispose()
        topic.dispose()
    }

    override fun onResume() {
        super.onResume()
        if (isPaused) {
            isPaused = false
            connectStomp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        stompClient.disconnect()
        stompConnection.dispose()
        topic.dispose()
        _binding = null
    }
}