package com.project.heyboardgame.main.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import com.project.heyboardgame.R
import com.project.heyboardgame.databinding.FragmentSearchBinding


class SearchFragment : Fragment(R.layout.fragment_search) {

    // View Binding
    private var _binding : FragmentSearchBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentSearchBinding.bind(view)

        binding.apply {
            backBtn.setOnClickListener {
                findNavController().popBackStack()
            }

        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}