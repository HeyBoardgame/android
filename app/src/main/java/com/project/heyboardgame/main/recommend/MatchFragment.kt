package com.project.heyboardgame.main.recommend

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.heyboardgame.R
import com.project.heyboardgame.adapter.GroupMatchRVAdapter
import com.project.heyboardgame.databinding.FragmentMatchBinding
import com.project.heyboardgame.main.MainViewModel

class MatchFragment : Fragment(R.layout.fragment_match) {
    // View Binding
    private var _binding : FragmentMatchBinding? = null
    private val binding get() = _binding!!
    // View Model
    private lateinit var mainViewModel: MainViewModel
    // Adapter
    private lateinit var groupMatchRVAdapter: GroupMatchRVAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        _binding = FragmentMatchBinding.bind(view)

        val args : MatchFragmentArgs by navArgs()
        val groupMatchResult = args.groupMatchResult

        binding.rematchBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.getRecommendBtn.setOnClickListener {
            val action = MatchFragmentDirections.actionMatchFragmentToMatchResFragment(groupMatchResult)
            findNavController().navigate(action)
        }

        groupMatchRVAdapter = GroupMatchRVAdapter(groupMatchResult.group)
        binding.nicknameRVList.adapter = groupMatchRVAdapter
        binding.nicknameRVList.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}