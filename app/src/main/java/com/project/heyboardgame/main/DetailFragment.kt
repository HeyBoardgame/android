package com.project.heyboardgame.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.project.heyboardgame.R
import com.project.heyboardgame.databinding.FragmentDetailBinding
import timber.log.Timber


class DetailFragment : Fragment(R.layout.fragment_detail) {

    // View Binding
    private var _binding : FragmentDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentDetailBinding.bind(view)

        binding.apply {
            backBtnDetail.setOnClickListener {
                // 이전 화면으로 이동
                //findNavController().popBackStack(findNavController().previousBackStackEntry?.destination!!.id, false)
                findNavController().popBackStack()
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}