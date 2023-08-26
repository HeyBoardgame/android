package com.project.heyboardgame.main.social


import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.project.heyboardgame.R
import com.project.heyboardgame.dataModel.FriendRequestData
import com.project.heyboardgame.databinding.FragmentAddFriendBinding
import com.project.heyboardgame.main.MainViewModel


class AddFriendFragment : Fragment(R.layout.fragment_add_friend) {
    // View Binding
    private var _binding : FragmentAddFriendBinding? = null
    private val binding get() = _binding!!
    // View Model
    private lateinit var mainViewModel: MainViewModel
    // 키보드 설정 변수
    private var originalMode : Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        originalMode = activity?.window?.getSoftInputMode()!!
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddFriendBinding.bind(view)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        binding.userCodeSearch.setOnQueryTextListener(searchViewTextListener)
        binding.userCodeSearch.isSubmitButtonEnabled = true // 검색창에 submit 버튼 보여줌

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private val searchViewTextListener: SearchView.OnQueryTextListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            mainViewModel.checkFriendRequest(query!!,
                onSuccess = {
                    showFriendConfirmationDialog(it, query!!)
                    binding.userCodeSearch.clearFocus()
                },
                onFailure = {
                    Toast.makeText(requireContext(), "해당 닉네임을 가진 사용자를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                    binding.userCodeSearch.clearFocus()
                },
                onErrorAction = {
                    Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    binding.userCodeSearch.clearFocus()
                }
            )
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            return false
        }
    }

    private fun Window.getSoftInputMode() : Int {
        return attributes.softInputMode
    }

    private fun showFriendConfirmationDialog(id: Int, nickname: String) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle("친구 추가 확인")
            .setMessage("'$nickname' 님을 친구로 추가하시겠습니까?")
            .setPositiveButton("확인") { _, _ ->
                val friendRequestData = FriendRequestData(id)
                mainViewModel.sendFriendRequest(friendRequestData,
                    onSuccess = {
                        Toast.makeText(requireContext(), "'$nickname' 님에게 친구 요청을 보냈습니다.", Toast.LENGTH_SHORT).show()
                        binding.userCodeSearch.setQuery("", false)
                        binding.userCodeSearch.clearFocus()
                    },
                    onFailure = {
                        Toast.makeText(requireContext(), "친구 목록에 있는 사용자이거나 이미 친구 요청을 보낸 사용자입니다.", Toast.LENGTH_SHORT).show()
                    },
                    onErrorAction = {
                        Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
                )
            }
            .setNegativeButton("취소") { _, _ ->
                Toast.makeText(requireContext(), "친구 요청이 취소되었습니다.", Toast.LENGTH_SHORT).show()
            }
            .create()
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        originalMode?.let { activity?.window?.setSoftInputMode(it) }
        _binding = null
    }
}