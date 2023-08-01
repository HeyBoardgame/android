package com.project.heyboardgame.main.profile

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.project.heyboardgame.R
import com.project.heyboardgame.dataModel.ChangePasswordData
import com.project.heyboardgame.databinding.FragmentChangePwdBinding
import com.project.heyboardgame.main.MainViewModel


class ChangePwdFragment : Fragment(R.layout.fragment_change_pwd) {
    // View Binding
    private var _binding : FragmentChangePwdBinding? = null
    private val binding get() = _binding!!
    // ViewModel
    private lateinit var mainViewModel: MainViewModel
    // 에러 상태 저장 변수
    private var isPasswordValid = false
    private var isPasswordCheckFail = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentChangePwdBinding.bind(view)

        binding.currentPassword.addTextChangedListener(currentPasswordCheckTextWatcher) // 현재 비밀번호 입력창에 TextWatcher 추가
        binding.newPassword.addTextChangedListener(newPasswordTextWatcher) // 새 비밀번호 입력창에 TextWatcher 추가
        binding.newPasswordCheck.addTextChangedListener(newPasswordCheckTextWatcher) // 새 비밀번호 확인 입력창에 TextWatcher 추가


        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.changePasswordBtn.setOnClickListener {
            val currentPassword = binding.currentPassword.text.toString()
            val newPassword = binding.newPassword.text.toString()
            val changePasswordData = ChangePasswordData(currentPassword, newPassword)
            mainViewModel.changePassword(changePasswordData,
                onSuccess = { // 비밀번호 변경 성공
                    Toast.makeText(requireContext(), "비밀번호가 성공적으로 변경되었습니다", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                },
                onFailure = { // 비밀번호 변경 실패
                    Toast.makeText(requireContext(), "현재 비밀번호가 일치하지 않습니다. 다시 입력해주세요.", Toast.LENGTH_SHORT).show()
                },
                onErrorAction = { // 네트워크 오류
                    Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private val currentPasswordCheckTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            updateChangePasswordButtonState()
        }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // 생략
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // 생략
        }
    }

    private val newPasswordTextWatcher = object : TextWatcher {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            validateNewPasswordCheck()
            if (binding.newPassword.text.toString().length < 8) {
                binding.passwordMinLength.visibility = View.VISIBLE
                isPasswordValid = false
            } else {
                binding.passwordMinLength.visibility = View.GONE
                isPasswordValid = true
            }
        }
        override fun afterTextChanged(s: Editable?) {
            validateNewPasswordCheck()
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // 생략
        }
    }

    private val newPasswordCheckTextWatcher = object : TextWatcher {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            validateNewPasswordCheck()
        }
        override fun afterTextChanged(s: Editable?) {
            validateNewPasswordCheck()
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // 생략
        }
    }

    // 새 비밀번호와 새 비밀번호 확인이 같은 지 체크하는 함수
    private fun validateNewPasswordCheck() {
        val newPassword = binding.newPassword.text.toString()
        val newPasswordCheck = binding.newPasswordCheck.text.toString()
        val isMatch = newPassword == newPasswordCheck

        if (isMatch) {
            // 새 비밀번호 확인이 일치하는 경우
            binding.passwordCheckFail.visibility = View.GONE
            isPasswordCheckFail = false
        } else {
            // 새 비밀번호 확인이 일치하지 않는 경우
            binding.passwordCheckFail.visibility = View.VISIBLE
            isPasswordCheckFail = true
        }
        updateChangePasswordButtonState()
    }

    // 모든 입력창에 값을 입력했는 지 확인하는 함수
    private fun isInputValid(): Boolean {
        val currentPassword = binding.currentPassword.text.toString()
        val newPassword = binding.newPassword.text.toString()
        val newPasswordCheck = binding.newPasswordCheck.text.toString()

        return currentPassword.isNotEmpty() && newPassword.isNotEmpty() && newPasswordCheck.isNotEmpty()
    }

    // 비밀번호 변경 버튼 상태 업데이트
    private fun updateChangePasswordButtonState() {
        binding.changePasswordBtn.isEnabled = isInputValid() && !isPasswordCheckFail && isPasswordValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}