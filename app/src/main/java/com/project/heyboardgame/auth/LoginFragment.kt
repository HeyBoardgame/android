package com.project.heyboardgame.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.project.heyboardgame.R
import com.project.heyboardgame.dataModel.GoogleLoginData
import com.project.heyboardgame.databinding.FragmentLoginBinding
import com.project.heyboardgame.dataModel.LoginData
import com.project.heyboardgame.main.MainActivity
import timber.log.Timber


class LoginFragment : Fragment() {
    // 뒤로 가기 이벤트를 위한 변수
    private lateinit var callback : OnBackPressedCallback
    private var backPressedTime : Long = 0
    // View Binding
    private var _binding : FragmentLoginBinding? = null
    private val binding get() = _binding!!
    // View Model
    private lateinit var authViewModel: AuthViewModel
    // Google Login
    private lateinit var googleSignInClient: GoogleSignInClient


    // 화면에서 뒤로 가기를 두 번 눌렀을 때 종료시켜주는 함수
    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (System.currentTimeMillis() - backPressedTime < 2500) {
                    activity?.finish()
                    return
                }
                Toast.makeText(activity, "한 번 더 누르면 앱이 종료됩니다.",Toast.LENGTH_SHORT).show()
                backPressedTime = System.currentTimeMillis()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        // 비밀번호 찾기 버튼 누를 때 발생하는 이벤트
        binding.findPwdBtn.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_forgotPwdFragment)
        }

        // 회원가입 버튼 누를 때 발생하는 이벤트
        binding.signUpBtn.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_signUpFragment1)
        }

        // 로그인 버튼 누를 때 발생하는 이벤트
        binding.loginBtn.setOnClickListener {
            val userId = binding.emailLogin.text.toString()
            val userPw = binding.pwdLogin.text.toString()

            // 이메일과 비밀번호 유효성 검사
            if (userId.isEmpty()) {
                Toast.makeText(requireContext(), "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else if (userPw.isEmpty()) {
                Toast.makeText(requireContext(), "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                val loginData = LoginData(userId, userPw)
                authViewModel.requestLogin(loginData,
                    onSuccess = {
                        Toast.makeText(requireContext(), "로그인 되었습니다.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    },
                    onFailure = {
                        Toast.makeText(requireContext(), "아이디 또는 비밀번호를 다시 한 번 확인해주세요.", Toast.LENGTH_SHORT).show()
                    },
                    onErrorAction = {
                        Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        binding.googleLoginBtn.setOnClickListener {
            // GoogleSignInOptions 설정
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()

            googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

            val signInIntent = googleSignInClient.signInIntent
            resultLauncher.launch(signInIntent)
        }
    }
    // 구글 로그인 결과 반환
    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        Timber.d("$result")
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    // 구글 로그인 결과 처리
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>){
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val googleEmail = account.email
            if (completedTask.isSuccessful) {
                val googleLoginData = GoogleLoginData(googleEmail!!)
                authViewModel.requestGoogleLogin(googleLoginData,
                    onSuccess = {
                        Toast.makeText(requireContext(), "로그인 되었습니다.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    },
                    onFailure = {
                        if (it == 401) {
                            Toast.makeText(requireContext(), "이미 가입된 회원입니다. 자체 로그인을 이용해주세요.", Toast.LENGTH_SHORT).show()
                        } else if (it == 404) {
                            Toast.makeText(requireContext(), "회원가입이 필요합니다.", Toast.LENGTH_SHORT).show()
                            val action = LoginFragmentDirections.actionLoginFragmentToGoogleSignUpFragment1(googleEmail)
                            findNavController().navigate(action)
                        } else {
                            Toast.makeText(requireContext(), "구글 로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onErrorAction = {
                        Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                Toast.makeText(requireContext(), "구글 로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: ApiException){
            Toast.makeText(requireContext(), "구글 로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }
}