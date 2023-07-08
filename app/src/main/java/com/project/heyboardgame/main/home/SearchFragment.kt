package com.project.heyboardgame.main.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import com.project.heyboardgame.R
import com.project.heyboardgame.databinding.FragmentSearchBinding


class SearchFragment : Fragment(R.layout.fragment_search) {

    // View Binding
    private var _binding : FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val numberOfPeople : Array<String> = resources.getStringArray(R.array.spinner_num)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentSearchBinding.bind(view)

//        binding.apply {
//            backBtnSearch.setOnClickListener {
//                findNavController().navigate(R.id.action_searchFragment_to_homeFragment)
//            }
//
//        }


    }

}