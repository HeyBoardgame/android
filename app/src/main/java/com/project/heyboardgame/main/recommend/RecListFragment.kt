package com.project.heyboardgame.main.recommend

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.heyboardgame.R
import com.project.heyboardgame.adapter.MatchResRVAdapter
import com.project.heyboardgame.dataModel.BoardGame2
import com.project.heyboardgame.databinding.FragmentRecListBinding
import com.project.heyboardgame.main.MainViewModel


class RecListFragment : Fragment(R.layout.fragment_rec_list) {
    // View Binding
    private var _binding : FragmentRecListBinding? = null
    private val binding get() = _binding!!
    // View Model
    private lateinit var mainViewModel: MainViewModel
    // Adapter
    private lateinit var matchResRVAdapter: MatchResRVAdapter
    // 그룹 추천 리스트
    private var recommends: List<BoardGame2> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        _binding = FragmentRecListBinding.bind(view)

        matchResRVAdapter = MatchResRVAdapter(recommends)

        mainViewModel.requestRecommendedList(
            onSuccess = {
                matchResRVAdapter.updateData(it.recommendations)
                binding.matchResultListRV.adapter = matchResRVAdapter
                binding.matchResultListRV.layoutManager = LinearLayoutManager(requireContext())
            },
            onFailure = {
                if (it == 404) {
                    binding.noContent.visibility = View.VISIBLE
                } else {
                    Toast.makeText(requireContext(), "추천 목록 불러오기에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            },
            onErrorAction = {
                Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        )

        matchResRVAdapter.setOnItemClickListener(object : MatchResRVAdapter.OnItemClickListener{
            override fun onItemClick(item: BoardGame2) {
                val action = RecListFragmentDirections.actionRecListFragmentToDetailFragment(item.id)
                findNavController().navigate(action)
            }
        })

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}