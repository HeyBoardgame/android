package com.project.heyboardgame.main.home

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.heyboardgame.R
import com.project.heyboardgame.adapter.HomeRVAdapter
import com.project.heyboardgame.dataModel.BoardgameList
import com.project.heyboardgame.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    // 뒤로 가기 이벤트를 위한 변수
    private lateinit var callback : OnBackPressedCallback
    private var backPressedTime : Long = 0
    // View Binding
    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!
    // Adapter
    private lateinit var homeRVAdapter: HomeRVAdapter


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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var boardgameList = mutableListOf<BoardgameList>()
        boardgameList.add(BoardgameList("@drawable/tmp_img", "티켓투라이드"))
        boardgameList.add(BoardgameList("@drawable/tmp_img", "티켓투라이드"))
        boardgameList.add(BoardgameList("@drawable/tmp_img", "티켓투라이드"))
        boardgameList.add(BoardgameList("@drawable/tmp_img", "티켓투라이드"))
        boardgameList.add(BoardgameList("@drawable/tmp_img", "티켓투라이드"))
        boardgameList.add(BoardgameList("@drawable/tmp_img", "티켓투라이드"))
        boardgameList.add(BoardgameList("@drawable/tmp_img", "티켓투라이드"))

        homeRVAdapter = HomeRVAdapter(requireContext(), boardgameList)
        binding.homeRV1.adapter = homeRVAdapter
        binding.homeRV1.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.homeRV2.adapter = homeRVAdapter
        binding.homeRV2.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.homeRV3.adapter = homeRVAdapter
        binding.homeRV3.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.homeRV4.adapter = homeRVAdapter
        binding.homeRV4.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.apply {
            searchBtn.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_searchFragment)

            }
        }

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