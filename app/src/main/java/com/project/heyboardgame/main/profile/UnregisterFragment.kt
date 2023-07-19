package com.project.heyboardgame.main.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.project.heyboardgame.R
import com.project.heyboardgame.auth.AuthActivity
import com.project.heyboardgame.databinding.FragmentUnregisterBinding
import com.project.heyboardgame.main.MainViewModel


class UnregisterFragment : Fragment(R.layout.fragment_unregister) {
    // View Binding
    private var _binding : FragmentUnregisterBinding? = null
    private val binding get() = _binding!!
    // ViewModel
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentUnregisterBinding.bind(view)

        binding.apply {
            backBtn.setOnClickListener {
                findNavController().popBackStack()
            }
            unregisterBtn.setOnClickListener {
                mainViewModel.requestUnregister(
                    onSuccess = {
                        val intent = Intent(requireContext(), AuthActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)

                        Toast.makeText(requireContext(), "회원 탈퇴가 완료되었습니다.", Toast.LENGTH_SHORT).show()
                    },
                    onFailure = {
                        Toast.makeText(requireContext(), "회원 탈퇴 실패", Toast.LENGTH_SHORT).show()
                    },
                    onErrorAction = {
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