package com.project.heyboardgame.main.home

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.heyboardgame.R
import com.project.heyboardgame.adapter.SearchRVAdapter
import com.project.heyboardgame.dataModel.SearchResultData
import com.project.heyboardgame.databinding.FragmentSearchBinding
import com.project.heyboardgame.main.MainViewModel


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
    private var isCardClicked = false
    private var isFamilyClicked = false
    private var isKidsClicked = false
    private var isWarClicked = false
    private var isWorldClicked = false
    // 장르 ID 리스트
    private var genreIdList : MutableList<Int> = mutableListOf()
    // 검색 결과 리스트
    private var searchResultList = mutableListOf<SearchResultData>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSearchBinding.bind(view)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        binding.search.setOnQueryTextListener(searchViewTextListener)
        binding.search.isSubmitButtonEnabled = true

        searchRVAdapter = SearchRVAdapter(requireContext(), searchResultList)
        binding.searchRV.adapter = searchRVAdapter
        binding.searchRV.layoutManager = LinearLayoutManager(requireContext())

        searchRVAdapter.itemClick = object : SearchRVAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {
                val id = searchResultList[position].id
                val action = SearchFragmentDirections.actionSearchFragmentToDetailFragment(id)
                findNavController().navigate(action)
            }
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
                    Toast.makeText(requireContext(), "인원 수: $numOfPlayer", Toast.LENGTH_SHORT).show()
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

            card.setOnClickListener {
                isCardClicked = if (isCardClicked) {
                    card.setBackgroundColor(Color.parseColor("#DADADA"))
                    cardIcon.setImageResource(R.drawable.icon_card)
                    cardText.setTextColor(Color.parseColor("#FF000000"))
                    false
                } else {
                    card.setBackgroundColor(Color.parseColor("#DEB4FF"))
                    cardIcon.setImageResource(R.drawable.icon_card_color)
                    cardText.setTextColor(Color.parseColor("#A93CFF"))
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

    // 키보드를 숨기는 메서드
    private fun hideKeyboard() {
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocusView = requireActivity().currentFocus
        if (currentFocusView != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocusView.windowToken, 0)
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
            hideKeyboard()
            if (isStrategyClicked) {
                genreIdList.add(1)
            }
            if (isPartyClicked) {
                genreIdList.add(2)
            }
            if (isSimpleClicked) {
                genreIdList.add(3)
            }
            if (isCardClicked) {
                genreIdList.add(4)
            }
            if (isFamilyClicked) {
                genreIdList.add(5)
            }
            if (isKidsClicked) {
                genreIdList.add(6)
            }
            if (isWarClicked) {
                genreIdList.add(7)
            }
            if (isWorldClicked) {
                genreIdList.add(8)
            }

            mainViewModel.requestSearchResult(query!!, genreIdList, numOfPlayer,
                onSuccess = {
                    if(it != null) { // 검색어와 일치하는 결과가 있는 경우
                        searchRVAdapter.setNewData(it)
                        searchResultList = it.toMutableList()
                    } else { // 검색어와 일치하는 결과가 없는 경우
                        Toast.makeText(requireContext(), "검색어와 일치하는 결과가 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                },
                onFailure = {
                    Toast.makeText(requireContext(), "검색에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                },
                onErrorAction = {
                    Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            )

            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            return false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}