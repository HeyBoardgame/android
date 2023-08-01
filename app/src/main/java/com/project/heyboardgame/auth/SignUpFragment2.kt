package com.project.heyboardgame.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.project.heyboardgame.R
import com.project.heyboardgame.adapter.SignUpRVAdapter
import com.project.heyboardgame.dataModel.SignUpData
import com.project.heyboardgame.dataModel.SignUpItem
import com.project.heyboardgame.databinding.FragmentSignUp2Binding

class SignUpFragment2 : Fragment() {

    // View Binding
    private var _binding : FragmentSignUp2Binding? = null
    private val binding get() = _binding!!
    // Adapter
    private lateinit var signUpRVAdapter : SignUpRVAdapter
    // View Model
    private lateinit var authViewModel: AuthViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSignUp2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        // safe args 사용해서 SignUpFragment1에서 email, password, nickname 받아오기
        val args: SignUpFragment2Args by navArgs()
        val signUpTmpData: SignUpData = args.signUpData

        val email = signUpTmpData.email
        val password = signUpTmpData.password
        val nickname = signUpTmpData.nickname

        val genreList = mutableListOf<SignUpItem>()
        genreList.add(SignUpItem(R.drawable.icon_strategy, "전략", 1, false))
        genreList.add(SignUpItem(R.drawable.icon_party, "파티", 2, false))
        genreList.add(SignUpItem(R.drawable.icon_simple, "단순", 3, false))
        genreList.add(SignUpItem(R.drawable.icon_card, "카드", 4, false))
        genreList.add(SignUpItem(R.drawable.icon_family, "가족", 5, false))
        genreList.add(SignUpItem(R.drawable.icon_kids, "키즈", 6, false))
        genreList.add(SignUpItem(R.drawable.icon_war, "전쟁", 7, false))
        genreList.add(SignUpItem(R.drawable.icon_world, "세계관", 8, false))

        signUpRVAdapter = SignUpRVAdapter(requireContext(), genreList)
        binding.genreListRV.adapter = signUpRVAdapter
        binding.genreListRV.layoutManager = GridLayoutManager(requireContext(), 2)

        // 뒤로가기 아이콘 누를 때 발생하는 이벤트
        binding.backBtn.setOnClickListener {
            Navigation.findNavController(view).popBackStack()
        }

        // 회원가입 버튼 누를 때 발생하는 이벤트
        binding.signUpBtn.setOnClickListener {
            val selectedItems = signUpRVAdapter.getSelectedItems()
            val signUpData = SignUpData(email, password, nickname, selectedItems)
            if (selectedItems.size < 3) {
                Toast.makeText(requireContext(), "3개 이상 골라주세요.", Toast.LENGTH_SHORT).show()
            } else {
                authViewModel.requestRegister(signUpData,
                    onSuccess = {
                        Toast.makeText(requireContext(), "회원 가입이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                        Navigation.findNavController(view).navigate(R.id.action_signUpFragment2_to_loginFragment)
                    },
                    onFailure = {
                        Toast.makeText(requireContext(), "회원 가입 실패", Toast.LENGTH_SHORT).show()
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