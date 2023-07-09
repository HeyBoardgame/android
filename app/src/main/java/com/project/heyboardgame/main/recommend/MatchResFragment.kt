package com.project.heyboardgame.main.recommend

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.heyboardgame.R
import com.project.heyboardgame.adapter.MatchResRVAdapter
import com.project.heyboardgame.dataModel.GroupMatchResult
import com.project.heyboardgame.databinding.FragmentMatchResBinding


class MatchResFragment : Fragment(R.layout.fragment_match_res) {

    // View Binding
    private var _binding : FragmentMatchResBinding? = null
    private val binding get() = _binding!!
    // Adapter
    private lateinit var matchResRVAdapter: MatchResRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentMatchResBinding.bind(view)

        binding.apply {
            // 두 번째 화면(추천 완료 화면)으로 이동
            backBtnMr.setOnClickListener {
                findNavController().popBackStack()
            }

            testBtn.setOnClickListener {
                findNavController().navigate(R.id.action_matchResFragment_to_detailFragment)
            }
        }

        var matchResList = mutableListOf<GroupMatchResult>()
        matchResList.add(GroupMatchResult("@drawable/logo_medium", "티켓투라이드", "전략, 단순", "어려움", "3~5인"))
        matchResList.add(GroupMatchResult("@drawable/logo_medium", "티켓투라이드", "전략, 단순", "어려움", "3~5인"))
        matchResList.add(GroupMatchResult("@drawable/logo_medium", "티켓투라이드", "전략, 단순", "어려움", "3~5인"))
        matchResList.add(GroupMatchResult("@drawable/logo_medium", "티켓투라이드", "전략, 단순", "어려움", "3~5인"))
        matchResList.add(GroupMatchResult("@drawable/logo_medium", "티켓투라이드", "전략, 단순", "어려움", "3~5인"))
        matchResList.add(GroupMatchResult("@drawable/logo_medium", "티켓투라이드", "전략, 단순", "어려움", "3~5인"))
        matchResList.add(GroupMatchResult("@drawable/logo_medium", "티켓투라이드", "전략, 단순", "어려움", "3~5인"))
        matchResList.add(GroupMatchResult("@drawable/logo_medium", "티켓투라이드", "전략, 단순", "어려움", "3~5인"))
        matchResList.add(GroupMatchResult("@drawable/logo_medium", "티켓투라이드", "전략, 단순", "어려움", "3~5인"))
        matchResList.add(GroupMatchResult("@drawable/logo_medium", "티켓투라이드", "전략, 단순", "어려움", "3~5인"))

        matchResRVAdapter = MatchResRVAdapter(requireContext(), matchResList)
        binding.matchResultListRV.adapter = matchResRVAdapter
        binding.matchResultListRV.layoutManager = LinearLayoutManager(requireContext())

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }



}