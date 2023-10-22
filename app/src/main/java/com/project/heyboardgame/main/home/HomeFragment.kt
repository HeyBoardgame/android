package com.project.heyboardgame.main.home

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.heyboardgame.App
import com.project.heyboardgame.R
import com.project.heyboardgame.adapter.HomeRVAdapter
import com.project.heyboardgame.dataModel.BoardGame
import com.project.heyboardgame.databinding.FragmentHomeBinding
import com.project.heyboardgame.main.MainActivity
import com.project.heyboardgame.main.MainViewModel


class HomeFragment : Fragment() {
    // 뒤로 가기 이벤트를 위한 변수
    private lateinit var callback : OnBackPressedCallback
    private var backPressedTime : Long = 0
    // View Binding
    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding!!
    // View Model
    private lateinit var mainViewModel: MainViewModel


    // 화면에서 뒤로 가기를 두 번 눌렀을 때 종료시켜주는 함수
    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (System.currentTimeMillis() - backPressedTime < 2500) {
                    activity?.finish()
                    return
                }
                Toast.makeText(activity, "한 번 더 누르면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show()
                backPressedTime = System.currentTimeMillis()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        mainViewModel.getPersonalRecommend(
            onSuccess = {
                val keys = it.keys
                val shelves = it.shelves
                val descriptions = it.descriptions

                val parentLayout = view.findViewById<LinearLayout>(R.id.parentLayout)

                keys.forEach { key ->
                    val shelfLayout = createShelf(requireContext(), descriptions[key]!!, shelves[key]!!)
                    parentLayout.addView(shelfLayout)
                }
            },
            onFailure = {
                Toast.makeText(requireContext(), "홈화면 불러오기에 실패했습니다.", Toast.LENGTH_SHORT).show()
            },
            onErrorAction = {
                Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        )

        binding.searchBtn.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }
    }

    private fun createShelf(context: Context, title: String, boardGames: List<BoardGame>): LinearLayout {
        // Shelf(LinearLayout) 생성
        val shelfLayout = LinearLayout(context)
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        layoutParams.bottomMargin = dpToPx(10)
        shelfLayout.layoutParams = layoutParams
        shelfLayout.orientation = LinearLayout.VERTICAL


        // Shelf 제목 TextView 생성
        val titleTextView = TextView(context)
        val titleLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        titleLayoutParams.leftMargin = dpToPx(10)
        titleTextView.layoutParams = titleLayoutParams

        // textView 속성 설정
        titleTextView.text = title
        titleTextView.setTextColor(Color.parseColor("#000000"))
        titleTextView.textSize = 18f
        titleTextView.setTypeface(titleTextView.typeface, Typeface.BOLD)

        // RecyclerView 생성 및 설정
        val recyclerView = RecyclerView(context)
        recyclerView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        // RecyclerView에 어댑터 및 레이아웃 매니저 설정
        val adapter = HomeRVAdapter(boardGames)
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener(object : HomeRVAdapter.OnItemClickListener {
            override fun onItemClick(item: BoardGame) {
                val action = HomeFragmentDirections.actionHomeFragmentToDetailFragment(item.id)
                findNavController().navigate(action)
            }
        })
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        val dividerView = View(context)
        val dividerLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            dpToPx(1)
        )
        dividerLayoutParams.topMargin = dpToPx(10)
        dividerLayoutParams.bottomMargin = dpToPx(10)
        dividerLayoutParams.leftMargin = dpToPx(10)
        dividerLayoutParams.rightMargin = dpToPx(10)
        dividerView.layoutParams = dividerLayoutParams
        dividerView.setBackgroundColor(ContextCompat.getColor(context, R.color.lightGrey)) // 배경색 설정

        // Shelf에 TextView와 RecyclerView 추가
        shelfLayout.addView(titleTextView)
        shelfLayout.addView(recyclerView)
        shelfLayout.addView(dividerView)

        return shelfLayout
    }

    private fun dpToPx(dp: Int): Int {
        val scale = context?.resources?.displayMetrics?.density
        return (dp * scale!! + 0.5f).toInt()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // 뒤로 가기 두 번을 위해 추가
    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }
}