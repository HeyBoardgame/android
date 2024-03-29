package com.project.heyboardgame.main.profile


import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.heyboardgame.R
import com.project.heyboardgame.adapter.HomeRVAdapter
import com.project.heyboardgame.dataModel.BoardGame
import com.project.heyboardgame.databinding.FragmentRatedBinding
import com.project.heyboardgame.main.MainViewModel


class RatedFragment : Fragment(R.layout.fragment_rated) {
    // View Binding
    private var _binding : FragmentRatedBinding? = null
    private val binding get() = _binding!!
    // ViewModel
    private lateinit var mainViewModel: MainViewModel
    // Adapter
    private lateinit var rated50Adapter: HomeRVAdapter
    private lateinit var rated45Adapter: HomeRVAdapter
    private lateinit var rated40Adapter: HomeRVAdapter
    private lateinit var rated35Adapter: HomeRVAdapter
    private lateinit var rated30Adapter: HomeRVAdapter
    private lateinit var rated25Adapter: HomeRVAdapter
    private lateinit var rated20Adapter: HomeRVAdapter
    private lateinit var rated15Adapter: HomeRVAdapter
    private lateinit var rated10Adapter: HomeRVAdapter
    private lateinit var rated05Adapter: HomeRVAdapter
    // 각 평점별 보드게임 목록
    private var rated50List: List<BoardGame> = emptyList()
    private var rated45List: List<BoardGame> = emptyList()
    private var rated40List: List<BoardGame> = emptyList()
    private var rated35List: List<BoardGame> = emptyList()
    private var rated30List: List<BoardGame> = emptyList()
    private var rated25List: List<BoardGame> = emptyList()
    private var rated20List: List<BoardGame> = emptyList()
    private var rated15List: List<BoardGame> = emptyList()
    private var rated10List: List<BoardGame> = emptyList()
    private var rated05List: List<BoardGame> = emptyList()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRatedBinding.bind(view)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        rated50Adapter = HomeRVAdapter(rated50List)
        rated45Adapter = HomeRVAdapter(rated45List)
        rated40Adapter = HomeRVAdapter(rated40List)
        rated35Adapter = HomeRVAdapter(rated35List)
        rated30Adapter = HomeRVAdapter(rated30List)
        rated25Adapter = HomeRVAdapter(rated25List)
        rated20Adapter = HomeRVAdapter(rated20List)
        rated15Adapter = HomeRVAdapter(rated15List)
        rated10Adapter = HomeRVAdapter(rated10List)
        rated05Adapter = HomeRVAdapter(rated05List)

        mainViewModel.getRatedList(
            onSuccess = {
                val boardGamesMap = it.boardGames

                rated50List = boardGamesMap[5.0f] ?: emptyList()
                rated45List = boardGamesMap[4.5f] ?: emptyList()
                rated40List = boardGamesMap[4.0f] ?: emptyList()
                rated35List = boardGamesMap[3.5f] ?: emptyList()
                rated30List = boardGamesMap[3.0f] ?: emptyList()
                rated25List = boardGamesMap[2.5f] ?: emptyList()
                rated20List = boardGamesMap[2.0f] ?: emptyList()
                rated15List = boardGamesMap[1.5f] ?: emptyList()
                rated10List = boardGamesMap[1.0f] ?: emptyList()
                rated05List = boardGamesMap[0.5f] ?: emptyList()

                if (rated50List.isEmpty()) {
                    binding.noRated50.visibility = View.VISIBLE
                    binding.rated50RV.visibility = View.GONE
                } else {
                    binding.noRated50.visibility = View.GONE
                    rated50Adapter.updateData(rated50List)
                    binding.rated50RV.adapter = rated50Adapter
                    binding.rated50RV.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                }

                if (rated45List.isEmpty()) {
                    binding.noRated45.visibility = View.VISIBLE
                    binding.rated45RV.visibility = View.GONE
                } else {
                    binding.noRated45.visibility = View.GONE
                    rated45Adapter.updateData(rated45List)
                    binding.rated45RV.adapter = rated45Adapter
                    binding.rated45RV.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                }

                if (rated40List.isEmpty()) {
                    binding.noRated40.visibility = View.VISIBLE
                    binding.rated40RV.visibility = View.GONE
                } else {
                    binding.noRated40.visibility = View.GONE
                    rated40Adapter.updateData(rated40List)
                    binding.rated40RV.adapter = rated40Adapter
                    binding.rated40RV.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                }

                if (rated35List.isEmpty()) {
                    binding.noRated35.visibility = View.VISIBLE
                    binding.rated35RV.visibility = View.GONE
                } else {
                    binding.noRated35.visibility = View.GONE
                    rated35Adapter.updateData(rated35List)
                    binding.rated35RV.adapter = rated35Adapter
                    binding.rated35RV.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                }

                if (rated30List.isEmpty()) {
                    binding.noRated30.visibility = View.VISIBLE
                    binding.rated30RV.visibility = View.GONE
                } else {
                    binding.noRated30.visibility = View.GONE
                    rated30Adapter.updateData(rated30List)
                    binding.rated30RV.adapter = rated30Adapter
                    binding.rated30RV.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                }

                if (rated25List.isEmpty()) {
                    binding.noRated25.visibility = View.VISIBLE
                    binding.rated25RV.visibility = View.GONE
                } else {
                    binding.noRated25.visibility = View.GONE
                    rated25Adapter.updateData(rated25List)
                    binding.rated25RV.adapter = rated25Adapter
                    binding.rated25RV.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                }

                if (rated20List.isEmpty()) {
                    binding.noRated20.visibility = View.VISIBLE
                    binding.rated20RV.visibility = View.GONE
                } else {
                    binding.noRated20.visibility = View.GONE
                    rated20Adapter.updateData(rated20List)
                    binding.rated20RV.adapter = rated20Adapter
                    binding.rated20RV.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                }

                if (rated15List.isEmpty()) {
                    binding.noRated15.visibility = View.VISIBLE
                    binding.rated15RV.visibility = View.GONE
                } else {
                    binding.noRated15.visibility = View.GONE
                    rated15Adapter.updateData(rated15List)
                    binding.rated15RV.adapter = rated15Adapter
                    binding.rated15RV.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                }

                if (rated10List.isEmpty()) {
                    binding.noRated10.visibility = View.VISIBLE
                    binding.rated10RV.visibility = View.GONE
                } else {
                    binding.noRated10.visibility = View.GONE
                    rated10Adapter.updateData(rated10List)
                    binding.rated10RV.adapter = rated10Adapter
                    binding.rated10RV.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                }

                if (rated05List.isEmpty()) {
                    binding.noRated05.visibility = View.VISIBLE
                    binding.rated05RV.visibility = View.GONE
                } else {
                    binding.noRated05.visibility = View.GONE
                    rated05Adapter.updateData(rated05List)
                    binding.rated05RV.adapter = rated05Adapter
                    binding.rated05RV.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                }
            },
            onFailure = {
                Toast.makeText(requireContext(), "평가한 보드게임 목록을 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
            },
            onErrorAction = {
                Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        )

        rated50Adapter.setOnItemClickListener(object : HomeRVAdapter.OnItemClickListener {
            override fun onItemClick(item: BoardGame) {
                val action = RatedFragmentDirections.actionRatedFragmentToDetailFragment(item.id)
                findNavController().navigate(action)
            }
        })
        rated45Adapter.setOnItemClickListener(object : HomeRVAdapter.OnItemClickListener {
            override fun onItemClick(item: BoardGame) {
                val action = RatedFragmentDirections.actionRatedFragmentToDetailFragment(item.id)
                findNavController().navigate(action)
            }
        })
        rated40Adapter.setOnItemClickListener(object : HomeRVAdapter.OnItemClickListener {
            override fun onItemClick(item: BoardGame) {
                val action = RatedFragmentDirections.actionRatedFragmentToDetailFragment(item.id)
                findNavController().navigate(action)
            }
        })
        rated35Adapter.setOnItemClickListener(object : HomeRVAdapter.OnItemClickListener {
            override fun onItemClick(item: BoardGame) {
                val action = RatedFragmentDirections.actionRatedFragmentToDetailFragment(item.id)
                findNavController().navigate(action)
            }
        })
        rated30Adapter.setOnItemClickListener(object : HomeRVAdapter.OnItemClickListener {
            override fun onItemClick(item: BoardGame) {
                val action = RatedFragmentDirections.actionRatedFragmentToDetailFragment(item.id)
                findNavController().navigate(action)
            }
        })
        rated25Adapter.setOnItemClickListener(object : HomeRVAdapter.OnItemClickListener {
            override fun onItemClick(item: BoardGame) {
                val action = RatedFragmentDirections.actionRatedFragmentToDetailFragment(item.id)
                findNavController().navigate(action)
            }
        })
        rated20Adapter.setOnItemClickListener(object : HomeRVAdapter.OnItemClickListener {
            override fun onItemClick(item: BoardGame) {
                val action = RatedFragmentDirections.actionRatedFragmentToDetailFragment(item.id)
                findNavController().navigate(action)
            }
        })
        rated15Adapter.setOnItemClickListener(object : HomeRVAdapter.OnItemClickListener {
            override fun onItemClick(item: BoardGame) {
                val action = RatedFragmentDirections.actionRatedFragmentToDetailFragment(item.id)
                findNavController().navigate(action)
            }
        })
        rated10Adapter.setOnItemClickListener(object : HomeRVAdapter.OnItemClickListener {
            override fun onItemClick(item: BoardGame) {
                val action = RatedFragmentDirections.actionRatedFragmentToDetailFragment(item.id)
                findNavController().navigate(action)
            }
        })
        rated05Adapter.setOnItemClickListener(object : HomeRVAdapter.OnItemClickListener {
            override fun onItemClick(item: BoardGame) {
                val action = RatedFragmentDirections.actionRatedFragmentToDetailFragment(item.id)
                findNavController().navigate(action)
            }
        })

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.more50Btn.setOnClickListener {
            val action = RatedFragmentDirections.actionRatedFragmentToSpecificRateFragment(5.0f)
            findNavController().navigate(action)
        }
        binding.more45Btn.setOnClickListener {
            val action = RatedFragmentDirections.actionRatedFragmentToSpecificRateFragment(4.5f)
            findNavController().navigate(action)
        }
        binding.more40Btn.setOnClickListener {
            val action = RatedFragmentDirections.actionRatedFragmentToSpecificRateFragment(4.0f)
            findNavController().navigate(action)
        }
        binding.more35Btn.setOnClickListener {
            val action = RatedFragmentDirections.actionRatedFragmentToSpecificRateFragment(3.5f)
            findNavController().navigate(action)
        }
        binding.more30Btn.setOnClickListener {
            val action = RatedFragmentDirections.actionRatedFragmentToSpecificRateFragment(3.0f)
            findNavController().navigate(action)
        }
        binding.more25Btn.setOnClickListener {
            val action = RatedFragmentDirections.actionRatedFragmentToSpecificRateFragment(2.5f)
            findNavController().navigate(action)
        }
        binding.more20Btn.setOnClickListener {
            val action = RatedFragmentDirections.actionRatedFragmentToSpecificRateFragment(2.0f)
            findNavController().navigate(action)
        }
        binding.more15Btn.setOnClickListener {
            val action = RatedFragmentDirections.actionRatedFragmentToSpecificRateFragment(1.5f)
            findNavController().navigate(action)
        }
        binding.more10Btn.setOnClickListener {
            val action = RatedFragmentDirections.actionRatedFragmentToSpecificRateFragment(1.0f)
            findNavController().navigate(action)
        }
        binding.more05Btn.setOnClickListener {
            val action = RatedFragmentDirections.actionRatedFragmentToSpecificRateFragment(0.5f)
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}