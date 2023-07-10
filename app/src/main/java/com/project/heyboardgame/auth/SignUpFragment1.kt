package com.project.heyboardgame.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.project.heyboardgame.R
import com.project.heyboardgame.databinding.FragmentSignUp1Binding


class SignUpFragment1 : Fragment() {

    // View Binding
    private var _binding : FragmentSignUp1Binding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSignUp1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 다음 단계 버튼 누를 때 발생하는 이벤트
        binding.nextBtn.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_signUpFragment1_to_signUpFragment2)
        }

        // 뒤로가기 아이콘 누를 때 발생하는 이벤트
        binding.backBtn.setOnClickListener {
            Navigation.findNavController(view).popBackStack()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}