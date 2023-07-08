package com.project.heyboardgame.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.project.heyboardgame.R
import com.project.heyboardgame.databinding.FragmentDetailBinding


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

    }


}