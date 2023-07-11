package com.project.heyboardgame.main.profile

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.project.heyboardgame.R
import com.project.heyboardgame.adapter.BadgeRVAdapter
import com.project.heyboardgame.databinding.FragmentProfileBinding


class ProfileFragment : Fragment() {

    // 뒤로 가기 이벤트를 위한 변수
    private lateinit var callback : OnBackPressedCallback
    private var backPressedTime : Long = 0

    // View Binding
    private var _binding : FragmentProfileBinding? = null
    private val binding get() = _binding!!
    // Adapter
    private lateinit var badgeRVAdapter: BadgeRVAdapter

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var badgeList = mutableListOf<String>()
        badgeList.add("뱃지")
        badgeList.add("뱃지")
        badgeList.add("뱃지")


        badgeRVAdapter = BadgeRVAdapter(requireContext(), badgeList)
        binding.badgeRV.adapter = badgeRVAdapter
        binding.badgeRV.layoutManager = GridLayoutManager(requireContext(), 3)

        binding.apply {
            favorite.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_detailFragment)
            }

            played.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_detailFragment)
            }

            evaluated.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_detailFragment)
            }

            recordPlay.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_detailFragment)
            }

            analysis.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_detailFragment)
            }

            challenge.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_detailFragment)
            }
        }
    }

    // 뒤로 가기 두 번을 위해 추가
    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }


}