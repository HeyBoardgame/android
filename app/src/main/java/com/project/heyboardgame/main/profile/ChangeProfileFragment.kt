package com.project.heyboardgame.main.profile


import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.project.heyboardgame.R
import com.project.heyboardgame.dataModel.ChangeProfileData
import com.project.heyboardgame.dataStore.MyDataStore
import com.project.heyboardgame.databinding.FragmentChangeProfileBinding
import com.project.heyboardgame.main.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


class ChangeProfileFragment : Fragment(R.layout.fragment_change_profile) {
    // View Binding
    private var _binding : FragmentChangeProfileBinding? = null
    private val binding get() = _binding!!
    // ViewModel
    private lateinit var mainViewModel: MainViewModel
    // 닉네임 에러 변수
    private var isNicknameInvalid = false
    private var isNicknameDuplicated = false
    private var isNicknameChanged = false
    // 이미지 변경 체크 변수
    private var isImageChanged = false
    // 프로필 이미지 변수
    private var finalImageUri: Uri? = null
    // 원래 닉네임 저장 변수
    private var originalNickname: String = ""
    // 키보드 설정 변수
    private var originalMode : Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        originalMode = activity?.window?.getSoftInputMode()!!
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentChangeProfileBinding.bind(view)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        val args : ChangeProfileFragmentArgs by navArgs()
        val myProfileData = args.myProfileData

        binding.nickname.setText(myProfileData.nickname)
        originalNickname = myProfileData.nickname
        if (myProfileData.profileImg != null) {
            Glide.with(requireContext())
                .load(myProfileData.profileImg)
                .into(binding.myProfileImg)
        } else {
            binding.myProfileImg.setImageResource(R.drawable.default_profile_img)
        }
        validateNickname(originalNickname)

        binding.nickname.addTextChangedListener(nicknameTextWatcher) // 닉네임 입력창에 TextWatcher 추가

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.changeProfileImg.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                galleryPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            else
                galleryPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        binding.defaultImgBtn.setOnClickListener {
            binding.myProfileImg.setImageResource(R.drawable.default_profile_img)
            finalImageUri = null
            isImageChanged = true
            updateChangeButtonState()
        }

        binding.nicknameCheckBtn.setOnClickListener {
            val nickname = binding.nickname.text.toString()
            mainViewModel.checkDuplicateNickname(nickname,
                onSuccess = {
                    binding.nicknameCheckSuccess.visibility = View.VISIBLE
                    binding.nicknameCheckFail.visibility = View.GONE
                    isNicknameDuplicated = false
                    updateChangeButtonState()
                },
                onFailure = {
                    binding.nicknameCheckSuccess.visibility = View.GONE
                    binding.nicknameCheckFail.visibility = View.VISIBLE
                    isNicknameDuplicated = true
                    updateChangeButtonState()
                },
                onErrorAction = {
                    Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            )
        }

        binding.changeProfileBtn.setOnClickListener {
            val filePart : MultipartBody.Part?
            val profileImg : String
            if (finalImageUri == null) {
                profileImg = ""
                filePart = null
            } else {
                profileImg = finalImageUri.toString()
                val file = File(absolutelyPath(finalImageUri, requireContext()))
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)
            }
            val nickname = binding.nickname.text.toString()
            val changeProfileData = ChangeProfileData(nickname, isImageChanged)


            mainViewModel.changeMyProfile(profileImg, filePart, changeProfileData,
                onSuccess = {
                    findNavController().popBackStack()
                    Toast.makeText(requireContext(), "프로필이 성공적으로 변경되었습니다.", Toast.LENGTH_SHORT).show()
                },
                onFailure = {
                    Toast.makeText(requireContext(), "프로필 변경 실패", Toast.LENGTH_SHORT).show()
                },
                onErrorAction = {
                    Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun Window.getSoftInputMode() : Int {
        return attributes.softInputMode
    }

    private fun absolutelyPath(path: Uri?, context : Context): String {
        val proj: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        val c: Cursor? = context.contentResolver.query(path!!, proj, null, null, null)
        val index = c?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        c?.moveToFirst()

        val result = c?.getString(index!!)

        return result!!
    }

    private val galleryPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){
            if (it){
                val intent = Intent(Intent.ACTION_PICK)
                intent.setDataAndType(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    "image/*"
                )
                imageLauncher.launch(intent)
            } else
                Toast.makeText(requireContext(), "갤러리 접근 권한이 필요합니다. 설정에서 권한을 허용해주세요.", Toast.LENGTH_SHORT).show()
        }

    private val imageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if (result.resultCode == RESULT_OK){
                val imageUri = result.data?.data
                binding.myProfileImg.setImageURI(imageUri)
                finalImageUri = imageUri
                isImageChanged = true
                updateChangeButtonState()
            }
        }

    // 닉네임 TextWatcher
    private val nicknameTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            binding.nicknameCheckSuccess.visibility = View.GONE
            binding.nicknameCheckFail.visibility = View.GONE
            isNicknameDuplicated = true
            isNicknameChanged = true
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

        if (nickname == originalNickname) {
            binding.nicknameCheckBtn.isEnabled = false
            isNicknameDuplicated = false
            isNicknameInvalid = false
            isNicknameChanged = false
            binding.nicknameInvalid.visibility = View.GONE
        } else {
            if (containsSpecialCharacters) {
                binding.nicknameInvalid.visibility = View.VISIBLE
                binding.nicknameCheckBtn.isEnabled = false
                isNicknameInvalid = true
            } else {
                binding.nicknameInvalid.visibility = View.GONE
                binding.nicknameCheckBtn.isEnabled = true
                isNicknameInvalid = false
            }
        }
        updateChangeButtonState()
    }

    private fun updateChangeButtonState() {
        binding.changeProfileBtn.isEnabled = (isImageChanged || isNicknameChanged) && !isNicknameInvalid && !isNicknameDuplicated
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        imageLauncher.unregister()
        originalMode?.let { activity?.window?.setSoftInputMode(it) }
    }
}