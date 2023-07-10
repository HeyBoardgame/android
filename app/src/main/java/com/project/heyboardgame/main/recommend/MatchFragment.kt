package com.project.heyboardgame.main.recommend

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.heyboardgame.R
import com.project.heyboardgame.adapter.GroupMatchRVAdapter
import com.project.heyboardgame.databinding.FragmentMatchBinding

class MatchFragment : Fragment(R.layout.fragment_match) {

    // View Binding
    private var _binding : FragmentMatchBinding? = null
    private val binding get() = _binding!!
    // Adapter
    private lateinit var groupMatchRVAdapter: GroupMatchRVAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentMatchBinding.bind(view)

        binding.apply {
            // 첫 번째 화면(추천 받는 화면)으로 이동
            rematchBtn.setOnClickListener {
                findNavController().popBackStack()
            }

            // 세 번째 화면(추천 결과 화면)으로 이동
            getRecommendBtn.setOnClickListener {
                findNavController().navigate(R.id.action_matchFragment_to_matchResFragment)
            }
        }

        var nicknameList = mutableListOf<String>()
        nicknameList.add("닉네임1")
        nicknameList.add("닉네임2")
        nicknameList.add("닉네임3")
        nicknameList.add("닉네임3")

        // Adapter 연결
        groupMatchRVAdapter = GroupMatchRVAdapter(requireContext(), nicknameList)
        binding.nicknameRVList.adapter = groupMatchRVAdapter
        binding.nicknameRVList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}