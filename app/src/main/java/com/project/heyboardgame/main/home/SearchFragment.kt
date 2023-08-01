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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSearchBinding.bind(view)

        binding.search.setOnQueryTextListener(searchViewTextListener)
        binding.search.isSubmitButtonEnabled = true

        searchRVAdapter = SearchRVAdapter(requireContext(), searchResultList)
        binding.searchRV.adapter = searchRVAdapter
        binding.searchRV.layoutManager = LinearLayoutManager(requireContext())

        searchRVAdapter.itemClick = object : SearchRVAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {
                val id = searchResultList[position].boardGameId
//                Toast.makeText(requireContext(), "${searchResultList[position].boardGameId}", Toast.LENGTH_SHORT).show()
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

//            Toast.makeText(requireContext(), "$query, $genreIdList, $numOfPlayer", Toast.LENGTH_SHORT).show()
//
//            val newResultList = mutableListOf<SearchResultData>()
//            newResultList.add(SearchResultData(11, "https://cf.geekdo-images.com/h8K_nTemD6bmydT0akChZw__itemrep/img/iMAU0kXMRnB-NLvM-KC92zxDPcg=/fit-in/246x300/filters:strip_icc()/pic3348790.jpg", "Unlock", listOf("전략", "파티", "가족"), "보통", "3~5인"))
//            newResultList.add(SearchResultData(1, "data:image/jpeg;base64,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", "Ticket to Ride", listOf("전략"), "어려움", "4인"))
//            newResultList.add(SearchResultData(28, "https://st4.depositphotos.com/4111759/23465/v/600/depositphotos_234653366-stock-illustration-board-game-vector-field-space.jpg", "TikiToffle", listOf("단순", "전략"), "쉬움", "3~5인"))
//            newResultList.add(SearchResultData(32, "https://st2.depositphotos.com/1526816/6541/v/950/depositphotos_65413847-stock-illustration-boardgame.jpg", "Blitz", listOf("단순"), "쉬움", "4인"))
//            newResultList.add(SearchResultData(49, "https://media.hellobot.co/fixedmenu/%E1%84%89%E1%85%B5%E1%84%85%E1%85%A9_%E1%84%8B%E1%85%A1%E1%84%86%E1%85%AE%20%E1%84%8B%E1%85%B5%E1%84%85%E1%85%B3%E1%86%B7%20%E1%84%8C%E1%85%B5%E1%86%BA%E1%84%80%E1%85%B5.png", "Zzzz", listOf("단순", "가족"), "쉬움", "4인"))
//            newResultList.add(SearchResultData(500, "https://pbs.twimg.com/media/EA9UJBjU4AAdkCm.jpg", "Azul", listOf("전략"), "어려움", "4인"))
//            newResultList.add(SearchResultData(65, "https://img.freepik.com/free-photo/cute-ai-generated-cartoon-bunny_23-2150288883.jpg", "Under the sea", listOf("키즈"), "쉬움", "2인"))
//            newResultList.add(SearchResultData(77, "https://mblogthumb-phinf.pstatic.net/MjAyMTA3MTRfMzUg/MDAxNjI2MjYyMDc5Nzc3.a2oZYaolOOHvW5rCZDDUJFiRJAvqN_mMnaB_NyJ4HqMg.VS6CekjVsm9_j3sjt3tq4ydl7ZeHzS5BvPhqA89yVNwg.PNG.gkfngkfn414/%EC%9D%BC%EB%9F%AC%EC%8A%A4%ED%8A%B8%EB%A0%88%EC%9D%B4%ED%84%B0_%EA%B7%80%EC%97%AC%EC%9A%B4_%EA%B7%B8%EB%A6%BC%EA%B7%B8%EB%A6%AC%EA%B8%B020.png?type=w800", "토크토크", listOf("키즈"), "쉬움", "2인"))
//            newResultList.add(SearchResultData(80, "https://img.freepik.com/premium-vector/cute-animal-vector-rabbit-cat-tiger-bear-chicken-cow-dog-panda-cute-illustration_493693-84.jpg", "티라노12", listOf("키즈"), "쉬움", "2인"))
//
//            searchRVAdapter.setNewData(newResultList)
//            searchResultList = newResultList

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