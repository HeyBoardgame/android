package com.project.heyboardgame.main.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.project.heyboardgame.R
import com.project.heyboardgame.auth.AuthActivity
import com.project.heyboardgame.dataModel.MyProfileResultData
import com.project.heyboardgame.dataStore.MyDataStore
import com.project.heyboardgame.databinding.FragmentProfileBinding
import com.project.heyboardgame.main.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber


class ProfileFragment : Fragment() {

    // 뒤로 가기 이벤트를 위한 변수
    private lateinit var callback : OnBackPressedCallback
    private var backPressedTime : Long = 0
    // View Binding
    private var _binding : FragmentProfileBinding? = null
    private val binding get() = _binding!!
    // View Model
    private lateinit var mainViewModel: MainViewModel
    // DataStore
    private val myDataStore : MyDataStore = MyDataStore()
    // 구글 로그인 유저인 지 확인하는 변수
    private var isGoogleLogined : Boolean = false
    // 나의 프로필 변수
    private var myProfileImg = ""
    private var myNickname = ""

    // 화면에서 뒤로 가기를 두 번 눌렀을 때 종료시켜주는 함수
    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (System.currentTimeMillis() - backPressedTime < 2500) {
                    activity?.finish()
                    return
                }
                Toast.makeText(activity, "한 번 더 누르면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show()
                backPressedTime = System.currentTimeMillis()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        mainViewModel.getMyProfile(
            onSuccess = {
                myNickname = it.nickname
                if (it.profileImg != null) {
                    myProfileImg = it.profileImg
                    Glide.with(requireContext())
                        .load(it.profileImg)
                        .into(binding.myProfileImg)
                } else {
                    myProfileImg = ""
                    binding.myProfileImg.setImageResource(R.drawable.default_profile_img)
                }
                binding.myNickname.text = it.nickname
            },
            onFailure = {
                Toast.makeText(requireContext(), "프로필 조회에 실패했습니다.", Toast.LENGTH_SHORT).show()
            },
            onErrorAction = {
                Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        )

        lifecycleScope.launch {
            val googleLogined = withContext(Dispatchers.IO) {
                myDataStore.getGoogleLogined()
            }
            isGoogleLogined = googleLogined
        }

        binding.bookmark.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_bookmarkFragment)
        }

        binding.rated.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_ratedFragment)
        }

        binding.logout.setOnClickListener {
            logoutGoogle()
            mainViewModel.requestLogout(
                onSuccess = {
                    Toast.makeText(requireContext(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(requireContext(), AuthActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                },
                onFailure = {
                    Toast.makeText(requireContext(), "로그아웃 실패", Toast.LENGTH_SHORT).show()
                },
                onErrorAction = {
                    Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            )
        }

        binding.changeProfile.setOnClickListener {
            val myProfileData : MyProfileResultData
            if (myProfileImg == "") {
                myProfileData = MyProfileResultData(null, myNickname)
            } else {
                myProfileData = MyProfileResultData(myProfileImg, myNickname)
            }
            val action = ProfileFragmentDirections.actionProfileFragmentToChangeProfileFragment(myProfileData)
            findNavController().navigate(action)
        }

        binding.changePassword.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_changePwdFragment)
        }

        binding.unregister.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_unregisterFragment)
        }

    }

    private fun logoutGoogle() {
        if (isGoogleLogined) {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()

            val googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

            googleSignInClient.signOut().addOnCompleteListener {
                lifecycleScope.launch {
                    myDataStore.setGoogleLogined(false)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // 뒤로 가기 두 번을 위해 추가
    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }
}