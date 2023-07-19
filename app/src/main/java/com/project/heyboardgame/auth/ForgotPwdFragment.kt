package com.project.heyboardgame.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.project.heyboardgame.App
import com.project.heyboardgame.databinding.FragmentForgotPwdBinding


class ForgotPwdFragment : Fragment() {

    // View Binding
    private var _binding : FragmentForgotPwdBinding? = null
    private val binding get() = _binding!!

    // View Model
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentForgotPwdBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 뒤로가기 아이콘 누를 때 발생하는 이벤트
        binding.backBtn.setOnClickListener {
            Navigation.findNavController(view).popBackStack()
        }

        binding.sendPwdBtn.setOnClickListener {
            val email = binding.emailForgotPwd.text.toString()

            if (email.isEmpty()) {
                Toast.makeText(requireContext(), "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                // 로딩 화면 표시
                binding.loading.visibility = View.VISIBLE

                authViewModel.requestTempPassword(email,
                    onSuccess = {
                        binding.sendPwdSuccess.visibility = View.VISIBLE
                        binding.sendPwdFail.visibility = View.GONE
                        // 로딩 화면 숨김
                        binding.loading.visibility = View.GONE
                    },
                    onFailure = {
                        binding.sendPwdSuccess.visibility = View.GONE
                        binding.sendPwdFail.visibility = View.VISIBLE
                        // 로딩 화면 숨김
                        binding.loading.visibility = View.GONE
                    },
                    onErrorAction = {
                        // 로딩 화면 숨김
                        binding.loading.visibility = View.GONE
                        Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}