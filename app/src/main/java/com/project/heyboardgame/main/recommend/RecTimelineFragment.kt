package com.project.heyboardgame.main.recommend

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.heyboardgame.R
import com.project.heyboardgame.adapter.TimelineRVAdapter
import com.project.heyboardgame.dataModel.Timeline
import com.project.heyboardgame.databinding.FragmentRecTimelineBinding
import com.project.heyboardgame.main.MainViewModel
import com.project.heyboardgame.utils.ViewUtils
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class RecTimelineFragment : Fragment(R.layout.fragment_rec_timeline) {
    // View Binding
    private var _binding : FragmentRecTimelineBinding? = null
    private val binding get() = _binding!!
    // View Model
    private lateinit var mainViewModel: MainViewModel
    // Adapter
    private lateinit var timelineRVAdapter: TimelineRVAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        _binding = FragmentRecTimelineBinding.bind(view)

        timelineRVAdapter = TimelineRVAdapter()
        binding.timelineRV.adapter = timelineRVAdapter
        binding.timelineRV.layoutManager = LinearLayoutManager(requireContext())

        timelineRVAdapter.setOnItemClickListener(object : TimelineRVAdapter.OnItemClickListener {
            override fun onItemClick(item: Timeline) {
                val action = RecTimelineFragmentDirections.actionRecTimelineFragmentToRecListFragment(item.timelineId)
                findNavController().navigate(action)
            }
        })

        loadTimelinePagingData()

        timelineRVAdapter.addLoadStateListener { loadStates ->
            val noContentView = binding.noContent
            ViewUtils.setNoContentListener(loadStates, noContentView, timelineRVAdapter.itemCount)
        }

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun loadTimelinePagingData() {
        mainViewModel.loadTimelinePagingData()

        mainViewModel.timelinePagingData.observe(viewLifecycleOwner) { pagingDataFlow ->
            viewLifecycleOwner.lifecycleScope.launch {
                pagingDataFlow.collectLatest { pagingData ->
                    timelineRVAdapter.submitData(pagingData)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}