package com.project.heyboardgame.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.project.heyboardgame.dataModel.GoogleRegisterTempData
import com.project.heyboardgame.databinding.FragmentGoogleSignUp1Binding


class GoogleSignUpFragment1 : Fragment() {
    // View Binding
    private var _binding : FragmentGoogleSignUp1Binding? = null
    private val binding get() = _binding!!
    // View Model
    private lateinit var authViewModel: AuthViewModel
    // 에러 변수
    private var isNicknameInvalid = false
    private var isNicknameDuplicated = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentGoogleSignUp1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        val args : GoogleSignUpFragment1Args by navArgs()
        val googleEmail = args.googleEmail

        binding.nickname.addTextChangedListener(nicknameTextWatcher) // 닉네임 입력창에 TextWatcher 추가

        binding.backBtn.setOnClickListener {
            Navigation.findNavController(view).popBackStack()
        }

        binding.nicknameCheck.setOnClickListener {
            val nickname = binding.nickname.text.toString()
            authViewModel.checkDuplicateNickname(nickname,
                onSuccess = {
                    binding.nicknameCheckSuccess.visibility = View.VISIBLE
                    binding.nicknameCheckFail.visibility = View.GONE
                    isNicknameDuplicated = false
                    updateNextButtonState()
                },
                onFailure = {
                    binding.nicknameCheckSuccess.visibility = View.GONE
                    binding.nicknameCheckFail.visibility = View.VISIBLE
                    isNicknameDuplicated = true
                    updateNextButtonState()
                },
                onErrorAction = {
                    Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            )
        }

        binding.nextBtn.setOnClickListener {
            val nickname = binding.nickname.text.toString()
            val googleRegisterTempData = GoogleRegisterTempData(googleEmail, nickname)
            val action = GoogleSignUpFragment1Directions.actionGoogleSignUpFragment1ToGoogleSignUpFragment2(googleRegisterTempData)
            findNavController().navigate(action)
        }

    }

    // 닉네임 TextWatcher
    private val nicknameTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            binding.nicknameCheckSuccess.visibility = View.GONE
            binding.nicknameCheckFail.visibility = View.GONE
            isNicknameDuplicated = true
            validateNickname(s.toString())
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // 생략
        }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // 생략
        }
    }

    // 닉네임 유효성 검사하는 함수
    private fun validateNickname(nickname: String) {
        val regex = Regex("^[A-Za-z0-9가-힣]+$") // 영문 대소문자, 숫자, 한글만 허용
        val containsSpecialCharacters = !regex.matches(nickname)

        if (containsSpecialCharacters) {
            binding.nicknameInvalid.visibility = View.VISIBLE
            binding.nicknameCheck.isEnabled = false
            isNicknameInvalid = true
        } else {
            binding.nicknameInvalid.visibility = View.GONE
            binding.nicknameCheck.isEnabled = true
            isNicknameInvalid = false
        }
        updateNextButtonState()
    }

    private fun updateNextButtonState() {
        binding.nextBtn.isEnabled = !isNicknameInvalid && !isNicknameDuplicated
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        googleSignInClient.signOut()
        _binding = null
    }
}