package com.project.heyboardgame.main.social

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.heyboardgame.R
import com.project.heyboardgame.adapter.FriendListRVAdapter
import com.project.heyboardgame.adapter.FriendRequestRVAdapter
import com.project.heyboardgame.databinding.FragmentSocialBinding


class SocialFragment : Fragment() {

    // 뒤로 가기 이벤트를 위한 변수
    private lateinit var callback : OnBackPressedCallback
    private var backPressedTime : Long = 0
    // View Binding
    private var _binding : FragmentSocialBinding? = null
    private val binding get() = _binding!!
    // Adapter
    private lateinit var friendListRVAdapter : FriendListRVAdapter
    private lateinit var friendRequestRVAdapter : FriendRequestRVAdapter
    private val layoutManager: RecyclerView.LayoutManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSocialBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        var nicknameList = mutableListOf<String>()
        nicknameList.add("닉네임1")
        nicknameList.add("닉네임2")
        nicknameList.add("닉네임3")
        nicknameList.add("닉네임4")
        nicknameList.add("닉네임5")

        friendRequestRVAdapter = FriendRequestRVAdapter(requireContext(), nicknameList)
        binding.friendRequestRV.adapter = friendRequestRVAdapter
        binding.friendRequestRV.layoutManager = LinearLayoutManager(requireContext())

        friendListRVAdapter = FriendListRVAdapter(requireContext(), nicknameList)
        binding.friendListRV.adapter = friendListRVAdapter
        binding.friendListRV.layoutManager = LinearLayoutManager(requireContext())

        binding.apply {
            addFriendBtn.setOnClickListener {
                findNavController().navigate(R.id.action_socialFragment_to_addFriendFragment)
            }
        }

    }

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

    // 뒤로 가기 두 번을 위해 추가
    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }




}