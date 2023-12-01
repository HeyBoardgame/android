package com.project.heyboardgame.main.home


import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.SeekBar
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.heyboardgame.R
import com.project.heyboardgame.adapter.SearchRVAdapter
import com.project.heyboardgame.dataModel.BoardGame2
import com.project.heyboardgame.databinding.FragmentSearchBinding
import com.project.heyboardgame.main.MainViewModel
import com.project.heyboardgame.utils.ViewUtils
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class SearchFragment : Fragment(R.layout.fragment_search) {
    // View Binding
    private var _binding : FragmentSearchBinding? = null
    private val binding get() = _binding!!
    // View Model
    private lateinit var mainViewModel: MainViewModel
    // Adapter
    private lateinit var searchRVAdapter: SearchRVAdapter
    // 장르 버튼 변수
    private var isStrategyClicked = false
    private var isPartyClicked = false
    private var isSimpleClicked = false
    private var isLicenseClicked = false
    private var isFamilyClicked = false
    private var isKidsClicked = false
    private var isWarClicked = false
    private var isWorldClicked = false
    // 장르 ID 리스트
    private var genreIdList : MutableList<Long> = mutableListOf()
    // 키보드 설정 변수
    private var originalMode : Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        originalMode = activity?.window?.getSoftInputMode()!!
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSearchBinding.bind(view)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        binding.search.setOnQueryTextListener(searchViewTextListener)
        binding.search.isSubmitButtonEnabled = true

        searchRVAdapter = SearchRVAdapter() // RecyclerView Adapter 초기화
        binding.searchRV.adapter = searchRVAdapter
        binding.searchRV.layoutManager = LinearLayoutManager(requireContext())

        val (keyword, genreIdList, numOfPlayer) = mainViewModel.getCurrentSearchQuery()
        if (keyword.isNotEmpty()) {
            loadSearchPagingData(keyword, genreIdList, numOfPlayer)
        }

        binding.numberText.text = numOfPlayer.toString()
        updateGenreUI(genreIdList)

        searchRVAdapter.setOnItemClickListener(object : SearchRVAdapter.OnItemClickListener {
            override fun onItemClick(item: BoardGame2) {
                val action = SearchFragmentDirections.actionSearchFragmentToDetailFragment(item.id)
                findNavController().navigate(action)
            }
        })

        searchRVAdapter.addLoadStateListener { loadStates ->
            val noContentView = binding.noContent
            ViewUtils.setNoContentListener(loadStates, noContentView, searchRVAdapter.itemCount)
        }

        binding.apply {
            backBtn.setOnClickListener {
                findNavController().popBackStack()
            }

            filterTitle.setOnClickListener {
                if (binding.filterItems.visibility == View.GONE) {
                    binding.filterItems.visibility = View.VISIBLE
                    binding.filterArrow.animate().setDuration(200).rotation(180f)
                } else {
                    binding.filterItems.visibility = View.GONE
                    binding.filterArrow.animate().setDuration(200).rotation(0f)
                }
            }

            seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    // 생략
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    // 생략
                }
                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    val numOfPlayer = seekBar?.progress
                    binding.numberText.text = numOfPlayer.toString()
                }

            })

            strategy.setOnClickListener {
                isStrategyClicked = if (isStrategyClicked) {
                    strategy.setBackgroundColor(Color.parseColor("#DADADA"))
                    strategyIcon.setImageResource(R.drawable.icon_strategy)
                    strategyText.setTextColor(Color.parseColor("#FF000000"))
                    false
                } else {
                    strategy.setBackgroundColor(Color.parseColor("#DEB4FF"))
                    strategyIcon.setImageResource(R.drawable.icon_strategy_color)
                    strategyText.setTextColor(Color.parseColor("#A93CFF"))
                    true
                }
            }

            party.setOnClickListener {
                isPartyClicked = if (isPartyClicked) {
                    party.setBackgroundColor(Color.parseColor("#DADADA"))
                    partyIcon.setImageResource(R.drawable.icon_party)
                    partyText.setTextColor(Color.parseColor("#FF000000"))
                    false
                } else {
                    party.setBackgroundColor(Color.parseColor("#DEB4FF"))
                    partyIcon.setImageResource(R.drawable.icon_party_color)
                    partyText.setTextColor(Color.parseColor("#A93CFF"))
                    true
                }
            }

            simple.setOnClickListener {
                isSimpleClicked = if (isSimpleClicked) {
                    simple.setBackgroundColor(Color.parseColor("#DADADA"))
                    simpleIcon.setImageResource(R.drawable.icon_simple)
                    simpleText.setTextColor(Color.parseColor("#FF000000"))
                    false
                } else {
                    simple.setBackgroundColor(Color.parseColor("#DEB4FF"))
                    simpleIcon.setImageResource(R.drawable.icon_simple_color)
                    simpleText.setTextColor(Color.parseColor("#A93CFF"))
                    true
                }
            }

            license.setOnClickListener {
                isLicenseClicked = if (isLicenseClicked) {
                    license.setBackgroundColor(Color.parseColor("#DADADA"))
                    licenseIcon.setImageResource(R.drawable.icon_license)
                    licenseText.setTextColor(Color.parseColor("#FF000000"))
                    false
                } else {
                    license.setBackgroundColor(Color.parseColor("#DEB4FF"))
                    licenseIcon.setImageResource(R.drawable.icon_license_color)
                    licenseText.setTextColor(Color.parseColor("#A93CFF"))
                    true
                }
            }

            family.setOnClickListener {
                isFamilyClicked = if (isFamilyClicked) {
                    family.setBackgroundColor(Color.parseColor("#DADADA"))
                    familyIcon.setImageResource(R.drawable.icon_family)
                    familyText.setTextColor(Color.parseColor("#FF000000"))
                    false
                } else {
                    family.setBackgroundColor(Color.parseColor("#DEB4FF"))
                    familyIcon.setImageResource(R.drawable.icon_family_color)
                    familyText.setTextColor(Color.parseColor("#A93CFF"))
                    true
                }
            }

            kids.setOnClickListener {
                isKidsClicked = if (isKidsClicked) {
                    kids.setBackgroundColor(Color.parseColor("#DADADA"))
                    kidsIcon.setImageResource(R.drawable.icon_kids)
                    kidsText.setTextColor(Color.parseColor("#FF000000"))
                    false
                } else {
                    kids.setBackgroundColor(Color.parseColor("#DEB4FF"))
                    kidsIcon.setImageResource(R.drawable.icon_kids_color)
                    kidsText.setTextColor(Color.parseColor("#A93CFF"))
                    true
                }
            }

            war.setOnClickListener {
                isWarClicked = if (isWarClicked) {
                    war.setBackgroundColor(Color.parseColor("#DADADA"))
                    warIcon.setImageResource(R.drawable.icon_war)
                    warText.setTextColor(Color.parseColor("#FF000000"))
                    false
                } else {
                    war.setBackgroundColor(Color.parseColor("#DEB4FF"))
                    warIcon.setImageResource(R.drawable.icon_war_color)
                    warText.setTextColor(Color.parseColor("#A93CFF"))
                    true
                }
            }

            world.setOnClickListener {
                isWorldClicked = if (isWorldClicked) {
                    world.setBackgroundColor(Color.parseColor("#DADADA"))
                    worldIcon.setImageResource(R.drawable.icon_world)
                    worldText.setTextColor(Color.parseColor("#FF000000"))
                    false
                } else {
                    world.setBackgroundColor(Color.parseColor("#DEB4FF"))
                    worldIcon.setImageResource(R.drawable.icon_world_color)
                    worldText.setTextColor(Color.parseColor("#A93CFF"))
                    true
                }
            }
        }
    }

    private val searchViewTextListener: SearchView.OnQueryTextListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            val numOfPlayer = binding.seekBar.progress
            genreIdList.clear()
            if (binding.filterItems.visibility == View.VISIBLE) {
                binding.filterItems.visibility = View.GONE
                binding.filterArrow.animate().setDuration(200).rotation(0f)
            }
            binding.search.clearFocus()
            if (isWorldClicked) {
                genreIdList.add(1)
            }
            if (isStrategyClicked) {
                genreIdList.add(2)
            }
            if (isWarClicked) {
                genreIdList.add(3)
            }
            if (isFamilyClicked) {
                genreIdList.add(4)
            }
            if (isLicenseClicked) {
                genreIdList.add(5)
            }
            if (isSimpleClicked) {
                genreIdList.add(6)
            }
            if (isPartyClicked) {
                genreIdList.add(7)
            }
            if (isKidsClicked) {
                genreIdList.add(8)
            }

            loadSearchPagingData(query!!, genreIdList, numOfPlayer)

            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            return false
        }
    }

    private fun Window.getSoftInputMode() : Int {
        return attributes.softInputMode
    }

    private fun loadSearchPagingData(keyword: String, genreIdList: List<Long>, numOfPlayer: Int) {
        mainViewModel.loadSearchPagingData(keyword, genreIdList, numOfPlayer)

        // SearchagingData Flow 관찰
        mainViewModel.searchPagingData.observe(viewLifecycleOwner) { pagingDataFlow ->
            viewLifecycleOwner.lifecycleScope.launch {
                pagingDataFlow.collectLatest { pagingData ->
                    searchRVAdapter.submitData(pagingData)
                }
            }
        }
    }

    private fun updateGenreUI(genreIdList: List<Long>) {
        if (genreIdList.contains(1)) {
            isWorldClicked = true
            binding.world.setBackgroundColor(Color.parseColor("#DEB4FF"))
            binding.worldIcon.setImageResource(R.drawable.icon_world_color)
            binding.worldText.setTextColor(Color.parseColor("#A93CFF"))
        }
        if (genreIdList.contains(2)) {
            isStrategyClicked = true
            binding.strategy.setBackgroundColor(Color.parseColor("#DEB4FF"))
            binding.strategyIcon.setImageResource(R.drawable.icon_strategy_color)
            binding.strategyText.setTextColor(Color.parseColor("#A93CFF"))
        }
        if (genreIdList.contains(3)) {
            isWarClicked = true
            binding.war.setBackgroundColor(Color.parseColor("#DEB4FF"))
            binding.warIcon.setImageResource(R.drawable.icon_war_color)
            binding.warText.setTextColor(Color.parseColor("#A93CFF"))
        }
        if (genreIdList.contains(4)) {
            binding.family.setBackgroundColor(Color.parseColor("#DEB4FF"))
            binding.familyIcon.setImageResource(R.drawable.icon_family_color)
            binding.familyText.setTextColor(Color.parseColor("#A93CFF"))
        }
        if (genreIdList.contains(5)) {
            binding.license.setBackgroundColor(Color.parseColor("#DEB4FF"))
            binding.licenseIcon.setImageResource(R.drawable.icon_license_color)
            binding.licenseText.setTextColor(Color.parseColor("#A93CFF"))
        }
        if (genreIdList.contains(6)) {
            binding.simple.setBackgroundColor(Color.parseColor("#DEB4FF"))
            binding.simpleIcon.setImageResource(R.drawable.icon_simple_color)
            binding.simpleText.setTextColor(Color.parseColor("#A93CFF"))
        }
        if (genreIdList.contains(7)) {
            binding.party.setBackgroundColor(Color.parseColor("#DEB4FF"))
            binding.partyIcon.setImageResource(R.drawable.icon_party_color)
            binding.partyText.setTextColor(Color.parseColor("#A93CFF"))
        }
        if (genreIdList.contains(8)) {
            binding.kids.setBackgroundColor(Color.parseColor("#DEB4FF"))
            binding.kidsIcon.setImageResource(R.drawable.icon_kids_color)
            binding.kidsText.setTextColor(Color.parseColor("#A93CFF"))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        originalMode?.let { activity?.window?.setSoftInputMode(it) }
        _binding = null
    }
}