package com.project.heyboardgame.main.social

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.heyboardgame.R
import com.project.heyboardgame.adapter.ChatListRVAdapter
import com.project.heyboardgame.dataModel.ChatRoom
import com.project.heyboardgame.dataModel.Friend
import com.project.heyboardgame.dataStore.MyDataStore
import com.project.heyboardgame.databinding.FragmentChatListBinding
import com.project.heyboardgame.main.MainViewModel
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import org.json.JSONObject
import timber.log.Timber
import java.util.concurrent.TimeUnit


class ChatListFragment : Fragment(R.layout.fragment_chat_list) {
    // View Binding
    private var _binding : FragmentChatListBinding? = null
    private val binding get() = _binding!!
    // Adapter
    private lateinit var chatListRVAdapter: ChatListRVAdapter
    // View Model
    private lateinit var mainViewModel: MainViewModel
    // DataStore
    private val myDataStore : MyDataStore = MyDataStore()
    // 액세스 토큰
    private var accessToken: String = ""
    // SSE
    private var eventSource: EventSource? = null
    private lateinit var client: OkHttpClient


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        _binding = FragmentChatListBinding.bind(view)

        accessToken = runBlocking { myDataStore.getAccessToken() }

        chatListRVAdapter = ChatListRVAdapter(mutableListOf())

        mainViewModel.getChatList(
            onSuccess = {
                if (it.isEmpty()) {
                    binding.noContent.visibility = View.VISIBLE
                } else {
                    chatListRVAdapter.addAll(it as MutableList<ChatRoom>)
                    binding.chatListRV.adapter = chatListRVAdapter
                    binding.chatListRV.layoutManager = LinearLayoutManager(requireContext())
                }
            },
            onFailure = {
                Toast.makeText(requireContext(), "채팅방 불러오기에 실패했습니다.", Toast.LENGTH_SHORT).show()
            },
            onErrorAction = {
                Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        )

        connectSSE()

        chatListRVAdapter.setOnItemClickListener(object : ChatListRVAdapter.OnItemClickListener {
            override fun onItemClick(item: ChatRoom) {
                val action = ChatListFragmentDirections.actionChatListFragmentToChatFragment(item.userInfo, item.roomId)
                findNavController().navigate(action)
            }
        })

        binding.addFriendBtn.setOnClickListener {
            findNavController().navigate(R.id.action_chatListFragment_to_addFriendFragment)
        }

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun connectSSE() {
        client = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.MINUTES)
            .writeTimeout(10, TimeUnit.MINUTES)
            .retryOnConnectionFailure(true)
            .build()

        val request = Request.Builder()
            .url("http://13.125.211.203:8080/api/v1/sse/subscribe")
            .header("Authorization", "Bearer $accessToken")
            .build()

        eventSource = EventSources.createFactory(client).newEventSource(request = request, listener = eventSourceListener)
    }

    private val eventSourceListener = object : EventSourceListener() {
        override fun onOpen(eventSource: EventSource, response: Response) {
            super.onOpen(eventSource, response)
            Timber.d("Connection Opened")
        }

        override fun onClosed(eventSource: EventSource) {
            super.onClosed(eventSource)
            Timber.d("Connection Closed")
        }

        override fun onEvent(eventSource: EventSource, id: String?, type: String?, data: String) {
            super.onEvent(eventSource, id, type, data)
            Timber.d("$id, $type, $data")
            try {
                val jsonObject = JSONObject(data)

                val roomId = jsonObject.getLong("id")
                val lastMessage = jsonObject.getString("lastMessage")
                val timestamp = jsonObject.getString("createdAt")
                val userInfoObject = jsonObject.getJSONObject("userInfo")
                val friendId = userInfoObject.getLong("id")
                val nickname = userInfoObject.getString("nickname")
                val image = userInfoObject.getString("imagePath")

                val friend = Friend(friendId, image, nickname)

                activity?.runOnUiThread {
                    chatListRVAdapter.addNewRoom(roomId, friend, lastMessage, timestamp)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
            super.onFailure(eventSource, t, response)
            Timber.d("SSE Failed: $t, $response")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        eventSource?.cancel()
    }
}