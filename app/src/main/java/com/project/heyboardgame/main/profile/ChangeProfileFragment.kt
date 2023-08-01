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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.project.heyboardgame.R
import com.project.heyboardgame.dataModel.ChangeProfileData
import com.project.heyboardgame.dataStore.MyDataStore
import com.project.heyboardgame.databinding.FragmentChangeProfileBinding
import com.project.heyboardgame.main.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


class ChangeProfileFragment : Fragment(R.layout.fragment_change_profile) {
    // View Binding
    private var _binding : FragmentChangeProfileBinding? = null
    private val binding get() = _binding!!
    // ViewModel
    private lateinit var mainViewModel: MainViewModel
    // DataStore
    private val myDataStore : MyDataStore = MyDataStore()
    // 에러 변수
    private var isNicknameInvalid = false
    // 변경 체크 변수
    private var isImageChanged = false
    // 프로필 이미지 변수
    private var finalImageUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentChangeProfileBinding.bind(view)
        lifecycleScope.launch {
            val profileImg = withContext(Dispatchers.IO) {
                myDataStore.getProfileImage()
            }
            val nickname = withContext(Dispatchers.IO) {
                myDataStore.getNickname()
            }
            binding.nickname.setText(nickname)
            if (profileImg != null) {
                Glide.with(requireContext())
                    .load(profileImg)
                    .into(binding.profileImg)
            } else {
                binding.profileImg.setImageResource(R.drawable.default_profile_img)
            }
        }

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
            binding.profileImg.setImageResource(R.drawable.default_profile_img)
            finalImageUri = null
            isImageChanged = true
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

    fun absolutelyPath(path: Uri?, context : Context): String {
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
                binding.profileImg.setImageURI(imageUri)
                finalImageUri = imageUri
                isImageChanged = true
            }
        }

    // 닉네임 TextWatcher
    private val nicknameTextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
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
            binding.changeProfileBtn.isEnabled = false
            isNicknameInvalid = true
        } else {
            binding.nicknameInvalid.visibility = View.GONE
            binding.changeProfileBtn.isEnabled = true
            isNicknameInvalid = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        imageLauncher.unregister()
    }
}