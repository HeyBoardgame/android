package com.project.heyboardgame.main.home

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.project.heyboardgame.R
import com.project.heyboardgame.dataModel.RatingData
import com.project.heyboardgame.databinding.FragmentDetailBinding
import com.project.heyboardgame.main.MainViewModel


class DetailFragment : Fragment(R.layout.fragment_detail) {

    // View Binding
    private var _binding : FragmentDetailBinding? = null
    private val binding get() = _binding!!
    // View Model
    private lateinit var mainViewModel: MainViewModel
    // 보드게임 ID
    private var id : Int = 0
    // 찜 여부
    private var isBookmarked : Boolean = false
    // 별점 관련 변수
    private var currentRating = 0f
    private var isRatingCanceled = false


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDetailBinding.bind(view)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        val args : DetailFragmentArgs by navArgs()
        id = args.id

        // 화면 초기화
        mainViewModel.requestDetail(id,
            onSuccess = {
                Glide.with(requireContext()).load(it.image).into(binding.detailImg) // 이미지
                binding.starRating.text = it.starRating.toString() // 보드게임 평점
                binding.title.text = it.title // 보드게임 이름
                binding.genre.text = it.genre.toCommaSeparatedString() // 장르
                binding.theme.text = it.theme.toCommaSeparatedString() // 테마
                binding.difficulty.text = it.difficulty // 난이도
                binding.timeRequired.text = it.timeRequired.toString() // 소요시간
                val max = it.playerMax
                val min = it.playerMin
                binding.numOfPlayer.text = createPlayerString(min, max) // 인원 수
                binding.detailDescription.text = it.description // 상세 설명
                binding.detailStrategy.text = it.strategy // 사용 전략
                isBookmarked = it.isBookmarked
                if (it.isBookmarked) {
                    binding.bookmarkBtn.setImageResource(R.drawable.icon_bookmark_full)
                } else {
                    binding.bookmarkBtn.setImageResource(R.drawable.icon_bookmark_empty)
                }
                currentRating = it.myRating.toFloat()
                binding.myRating.rating = it.myRating.toFloat()
            },
            onFailure = {
                Toast.makeText(requireContext(), "상세 조회 실패", Toast.LENGTH_SHORT).show()
            },
            onErrorAction = {
                Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        )

        // 뒤로가기 버튼
        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        // 찜하기 버튼
        binding.bookmarkBtn.setOnClickListener {
            mainViewModel.requestBookmark(id,
                onSuccess = {
                    if (isBookmarked) { // 원래 찜하기가 되어 있었던 경우
                        isBookmarked = false
                        binding.bookmarkBtn.setImageResource(R.drawable.icon_bookmark_empty)
                        Toast.makeText(requireContext(), "찜한 보드게임에서 삭제했습니다.", Toast.LENGTH_SHORT).show()
                    } else { // 원래 찜하기가 안 되어 있었던 경우
                        isBookmarked = true
                        binding.bookmarkBtn.setImageResource(R.drawable.icon_bookmark_full)
                        Toast.makeText(requireContext(), "찜한 보드게임에 추가했습니다.", Toast.LENGTH_SHORT).show()
                    }
                },
                onFailure = {
                    Toast.makeText(requireContext(), "찜하기에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                },
                onErrorAction = {
                    Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            )
        }

        // 별점 남기기
        binding.myRating.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            if (isRatingCanceled) {
                isRatingCanceled = false
            } else {
                showRatingDialog(rating)
            }
        }
    }

    // List의 [ ] 지워 주는 함수
    private fun List<String>.toCommaSeparatedString(): String {
        return joinToString(", ")
    }

    // 인원 수 String 만드는 함수
    private fun createPlayerString(min: Int, max: Int): String {
        return if (min == max) {
            "${min}인"
        } else {
            "${min}~${max}인"
        }
    }

    // 별점 남기기 Dialog + 서버 통신
    private fun showRatingDialog(changedRating : Float) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle("별점 남기기")
            .setMessage("별점을 남기시겠습니까?")
            .setPositiveButton("확인") { _, _ ->
                val ratingData = RatingData(changedRating)
                mainViewModel.requestRating(id, ratingData,
                    onSuccess = {
                        Toast.makeText(requireContext(), "별점이 성공적으로 등록되었습니다.", Toast.LENGTH_SHORT).show()
                        currentRating = changedRating
                        isRatingCanceled = false
                    },
                    onFailure = {
                        Toast.makeText(requireContext(), "별점 등록에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                        isRatingCanceled = true
                        binding.myRating.rating = currentRating
                    },
                    onErrorAction = {
                        Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
                )
            }
            .setNegativeButton("취소") { _, _ ->
                isRatingCanceled = true
                binding.myRating.rating = currentRating
            }
            .create()
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}