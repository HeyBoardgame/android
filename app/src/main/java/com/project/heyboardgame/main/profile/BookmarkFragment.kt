package com.project.heyboardgame.main.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import com.project.heyboardgame.R
import com.project.heyboardgame.adapter.HistoryRVAdapter
import com.project.heyboardgame.dataModel.BoardGame
import com.project.heyboardgame.databinding.FragmentBookmarkBinding
import com.project.heyboardgame.main.MainViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class BookmarkFragment : Fragment(R.layout.fragment_bookmark) {
    // View Binding
    private var _binding : FragmentBookmarkBinding? = null
    private val binding get() = _binding!!
    // ViewModel
    private lateinit var mainViewModel: MainViewModel
    // Adapter
    private lateinit var historyRVAdapter: HistoryRVAdapter
    // sort 변수
    private var selectedSort: String = "new"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentBookmarkBinding.bind(view)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        // RecyclerView Adapter 초기화
        historyRVAdapter = HistoryRVAdapter()

        binding.bookmarkRV.adapter = historyRVAdapter
        binding.bookmarkRV.layoutManager = GridLayoutManager(requireContext(), 3)

        historyRVAdapter.setOnItemClickListener(object : HistoryRVAdapter.OnItemClickListener {
            override fun onItemClick(item: BoardGame) {
                val action = BookmarkFragmentDirections.actionBookmarkFragmentToDetailFragment(item.id)
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
                loadBookmarkPagingData(selectedSort)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 생략
            }
        }
    }

    private fun loadBookmarkPagingData(sort: String) {
        mainViewModel.loadBookmarkPagingData(sort)

        // BookmarkPagingData Flow 관찰
        mainViewModel.bookmarkPagingData.observe(viewLifecycleOwner) { pagingDataFlow ->
            viewLifecycleOwner.lifecycleScope.launch {
                pagingDataFlow.collectLatest { pagingData ->
                    // 데이터를 지우고 다시 로드
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