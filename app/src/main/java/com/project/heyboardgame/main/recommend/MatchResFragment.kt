package com.project.heyboardgame.main.recommend


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.heyboardgame.R
import com.project.heyboardgame.adapter.MatchResRVAdapter
import com.project.heyboardgame.dataModel.BoardGame2
import com.project.heyboardgame.dataModel.GroupRecommendData
import com.project.heyboardgame.databinding.FragmentMatchResBinding
import com.project.heyboardgame.main.MainViewModel


class MatchResFragment : Fragment(R.layout.fragment_match_res) {
    // View Binding
    private var _binding : FragmentMatchResBinding? = null
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
        _binding = FragmentMatchResBinding.bind(view)

        matchResRVAdapter = MatchResRVAdapter(recommends)

        val args : MatchResFragmentArgs by navArgs()
        val groupMatchResult = args.groupMatchResult

        val seed = groupMatchResult.seed
        val idList = mutableListOf<Long>()

        for (i in 0 until groupMatchResult.group.size) {
            val id = groupMatchResult.group[i].id
            idList.add(id)
        }

        val groupRecommendData = GroupRecommendData(idList, seed)

        mainViewModel.requestGroupRecommend(groupRecommendData,
            onSuccess = {
                matchResRVAdapter.updateData(it.recommendations)
                binding.matchResultListRV.adapter = matchResRVAdapter
                binding.matchResultListRV.layoutManager = LinearLayoutManager(requireContext())
            },
            onFailure = {
                Toast.makeText(requireContext(), "그룹 추천 결과 불러오기에 실패했습니다.", Toast.LENGTH_SHORT).show()
            },
            onErrorAction = {
                Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        )

        matchResRVAdapter.setOnItemClickListener(object : MatchResRVAdapter.OnItemClickListener{
            override fun onItemClick(item: BoardGame2) {
                val action = MatchResFragmentDirections.actionMatchResFragmentToDetailFragment(item.id)
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