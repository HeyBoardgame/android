package com.project.heyboardgame.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.project.heyboardgame.R
import com.project.heyboardgame.adapter.SignUpRVAdapter
import com.project.heyboardgame.databinding.FragmentSignUp2Binding


class SignUpFragment2 : Fragment() {

    // View Binding
    private var _binding : FragmentSignUp2Binding? = null
    private val binding get() = _binding!!
    // Adapter
    private lateinit var signUpRVAdapter : SignUpRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSignUp2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var testList = mutableListOf<String>()
        testList.add("장르1")
        testList.add("장르2")
        testList.add("장르3")
        testList.add("장르4")
        testList.add("장르5")
        testList.add("장르6")
        testList.add("장르7")
        testList.add("장르8")

        signUpRVAdapter = SignUpRVAdapter(requireContext(), testList)
        binding.genreListRV.adapter = signUpRVAdapter
        binding.genreListRV.layoutManager = GridLayoutManager(requireContext(), 2)

        // 뒤로가기 아이콘 누를 때 발생하는 이벤트
        binding.backBtn.setOnClickListener {
            Navigation.findNavController(view).popBackStack()
        }

        // 회원가입 버튼 누를 때 발생하는 이벤트
        binding.signUpBtn.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_signUpFragment2_to_loginFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}