package com.project.heyboardgame.main.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import com.project.heyboardgame.R
import com.project.heyboardgame.adapter.HistoryRVAdapter
import com.project.heyboardgame.dataModel.BoardGame
import com.project.heyboardgame.databinding.FragmentSpecificRateBinding
import com.project.heyboardgame.main.MainViewModel
import com.project.heyboardgame.utils.ViewUtils
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class SpecificRateFragment : Fragment(R.layout.fragment_specific_rate) {
    // View Binding
    private var _binding : FragmentSpecificRateBinding? = null
    private val binding get() = _binding!!
    // ViewModel
    private lateinit var mainViewModel: MainViewModel
    // Adapter
    private lateinit var historyRVAdapter: HistoryRVAdapter
    // sort 변수
    private var selectedSort: String = "new"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSpecificRateBinding.bind(view)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        val args : SpecificRateFragmentArgs by navArgs()
        val score = args.score

        binding.score.text = score.toString()

        historyRVAdapter = HistoryRVAdapter() // RecyclerView Adapter 초기화
        binding.ratedRV.adapter = historyRVAdapter
        binding.ratedRV.layoutManager = GridLayoutManager(requireContext(), 3)

        historyRVAdapter.setOnItemClickListener(object : HistoryRVAdapter.OnItemClickListener {
            override fun onItemClick(item: BoardGame) {
                val action = SpecificRateFragmentDirections.actionSpecificRateFragmentToDetailFragment(item.id)
                findNavController().navigate(action)
            }
        })

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        // Spinner 초기화
        val spinnerItems = arrayOf("최신순", "인기순", "쉬운순", "어려운순")
        val spinnerAdapter = ArrayAdapter(requireContext(), R.layout.item_spinner, spinnerItems)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.sortDropdown.adapter = spinnerAdapter

        // Spinner 선택 이벤트 처리
        binding.sortDropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = spinnerItems[position]
                selectedSort = when (selectedItem) {
                    "최신순" -> "new"
                    "인기순" -> "popular"
                    "쉬운순" -> "easy"
                    "어려운순" -> "hard"
                    else -> "new"
                }
                // 페이징 데이터 로드 시작
                loadRatedPagingData(score, selectedSort)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 생략
            }
        }

        // Adapter 아이템 개수 리스너 설정
        historyRVAdapter.addLoadStateListener { loadStates ->
            val noContentView = binding.noContent
            ViewUtils.setNoContentListener(loadStates, noContentView, historyRVAdapter.itemCount)
        }
    }

    private fun loadRatedPagingData(score: Float, sort: String) {
        mainViewModel.loadRatedPagingData(score, sort)

        // RatedPagingData Flow 관찰
        mainViewModel.ratedPagingData.observe(viewLifecycleOwner) { pagingDataFlow ->
            viewLifecycleOwner.lifecycleScope.launch {
                pagingDataFlow.collectLatest { pagingData ->
                    historyRVAdapter.submitData(PagingData.empty())
                    historyRVAdapter.submitData(pagingData)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}