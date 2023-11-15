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
import com.project.heyboardgame.dataModel.Friend
import com.project.heyboardgame.dataModel.RatingData
import com.project.heyboardgame.databinding.FragmentDetailBinding
import com.project.heyboardgame.main.MainViewModel
import com.project.heyboardgame.utils.GlideUtils
import timber.log.Timber
import kotlin.math.round


class DetailFragment : Fragment(R.layout.fragment_detail) {
    // View Binding
    private var _binding : FragmentDetailBinding? = null
    private val binding get() = _binding!!
    // View Model
    private lateinit var mainViewModel: MainViewModel
    // 보드게임 ID
    private var id : Long = 0
    // 찜 여부
    private var isBookmarked : Boolean = false
    // 별점 관련 변수
    private var isFirstTime = true
    private var currentRating = 0f
    private var isRatingCanceled = false
    // 친구 아이디
    private lateinit var bestRatingFriend : Friend
    private lateinit var worstRatingFriend : Friend


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentDetailBinding.bind(view)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        val args : DetailFragmentArgs by navArgs()
        id = args.id
        isFirstTime = true
        // 화면 초기화
        mainViewModel.getDetail(id,
            onSuccess = {
                // 보드게임 정보 세팅
                Glide.with(requireContext()).load(it.boardGameDetail.image).into(binding.detailImg) // 이미지
                binding.starRating.text = roundDecimals(it.boardGameDetail.starRating).toString()// 보드게임 평점
                binding.title.text = it.boardGameDetail.title // 보드게임 이름
                binding.genre.text = it.boardGameDetail.genre.toCommaSeparatedString() // 장르
                binding.theme.text = it.boardGameDetail.theme.toCommaSeparatedString() // 테마
                binding.difficulty.text = it.boardGameDetail.difficulty // 난이도
                binding.timeRequired.text = it.boardGameDetail.playTime.toString() // 소요시간
                val max = it.boardGameDetail.playerMax
                val min = it.boardGameDetail.playerMin
                binding.numOfPlayer.text = createPlayerString(min, max) // 인원 수
                binding.detailDescription.text = it.boardGameDetail.description // 상세 설명
                binding.detailStrategy.text = it.boardGameDetail.strategy.toCommaSeparatedString() // 사용 전략
                isBookmarked = it.myDetail.isBookmarked
                if (it.boardGameDetail.translated == "YES") {
                    binding.translated.text = "O"
                } else {
                    binding.translated.text = "X"
                }
                // 내 정보 세팅
                if (it.myDetail.isBookmarked) {
                    binding.bookmarkBtn.setImageResource(R.drawable.icon_bookmark_full)
                } else {
                    binding.bookmarkBtn.setImageResource(R.drawable.icon_bookmark_empty)
                }
                if (it.myDetail.myRating != null) {
                    currentRating = it.myDetail.myRating.toFloat()
                    binding.myRating.rating = it.myDetail.myRating.toFloat()
                    isFirstTime = false
                } else {
                    currentRating = 0F
                    binding.myRating.rating = 0F
                    isFirstTime = false
                }
                // 친구 정보 세팅
                if (it.bestRatingFriend != null && it.worstRatingFriend != null) {
                    binding.friendsRating.visibility = View.VISIBLE
                    bestRatingFriend = Friend(it.bestRatingFriend.friendId, it.bestRatingFriend.image, it.bestRatingFriend.nickname)
                    worstRatingFriend = Friend(it.worstRatingFriend.friendId, it.worstRatingFriend.image, it.worstRatingFriend.nickname)
                    GlideUtils.loadThumbnailImage(requireContext(), it.bestRatingFriend.image, binding.bestProfileImg)
                    GlideUtils.loadThumbnailImage(requireContext(), it.worstRatingFriend.image, binding.worstProfileImg)
                    binding.bestNickname.text = it.bestRatingFriend.nickname
                    binding.worstNickname.text = it.worstRatingFriend.nickname
                    binding.bestRate.text = it.bestRatingFriend.rating.toString()
                    binding.worstRate.text = it.worstRatingFriend.rating.toString()
                } else {
                    binding.friendsRating.visibility = View.GONE
                }
            },
            onFailure = {
                Toast.makeText(requireContext(), "상세 조회 실패", Toast.LENGTH_SHORT).show()
            },
            onErrorAction = {
                Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        )

        binding.descriptionTitle.setOnClickListener {
            if (binding.detailDescription.visibility == View.GONE) {
                binding.detailDescription.visibility = View.VISIBLE
                binding.descriptionArrow.animate().setDuration(200).rotation(180f)
            } else {
                binding.detailDescription.visibility = View.GONE
                binding.descriptionArrow.animate().setDuration(200).rotation(0f)
            }
        }

        // 뒤로가기 버튼
        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.askBestReview.setOnClickListener {
            mainViewModel.requestRoomId(bestRatingFriend.id,
                onSuccess = {
                    val action = DetailFragmentDirections.actionDetailFragmentToChatFragment(bestRatingFriend, it)
                    findNavController().navigate(action)
                },
                onFailure = {
                    Toast.makeText(requireContext(), "채팅방 아이디 불러오기에 실패했습니다.", Toast.LENGTH_SHORT).show()
                },
                onErrorAction = {
                    Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            )
        }

        binding.askWorstReview.setOnClickListener {
            mainViewModel.requestRoomId(worstRatingFriend.id,
                onSuccess = {
                    val action = DetailFragmentDirections.actionDetailFragmentToChatFragment(worstRatingFriend, it)
                    findNavController().navigate(action)
                },
                onFailure = {
                    Toast.makeText(requireContext(), "채팅방 아이디 불러오기에 실패했습니다.", Toast.LENGTH_SHORT).show()
                },
                onErrorAction = {
                    Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                }
            )
        }

        // 찜하기 버튼
        binding.bookmarkBtn.setOnClickListener {
            if (isBookmarked) { // 원래 찜하기가 되어 있었던 경우
                mainViewModel.deleteBookmark(id,
                    onSuccess = {
                        isBookmarked = false
                        binding.bookmarkBtn.setImageResource(R.drawable.icon_bookmark_empty)
                        Toast.makeText(requireContext(), "찜한 보드게임에서 삭제했습니다.", Toast.LENGTH_SHORT).show()
                    },
                    onFailure = {
                        Toast.makeText(requireContext(), "찜하기 취소에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                    },
                    onErrorAction = {
                        Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
                )
            } else { // 찜하기가 안 되어 있었던 경우
                mainViewModel.addBookmark(id,
                    onSuccess = {
                        isBookmarked = true
                        binding.bookmarkBtn.setImageResource(R.drawable.icon_bookmark_full)
                        Toast.makeText(requireContext(), "찜한 보드게임에 추가했습니다.", Toast.LENGTH_SHORT).show()
                    },
                    onFailure = {
                        Toast.makeText(requireContext(), "찜하기에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                    },
                    onErrorAction = {
                        Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        // 평점 남기기
        binding.myRating.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            if (!isFirstTime) {
                if (isRatingCanceled) {
                    isRatingCanceled = false
                } else {
                    showRatingDialog(rating)
                }
            }
        }
    }

    // List의 [ ] 지워 주는 함수
    private fun List<String>.toCommaSeparatedString(): String {
        return joinToString(", ")
    }

    private fun roundDecimals(value: Double): Double {
        return round(value * 10.0) / 10.0
    }

    // 인원 수 String 만드는 함수
    private fun createPlayerString(min: Int, max: Int): String {
        return if (min == max) {
            "${min}인"
        } else {
            "${min}~${max}인"
        }
    }

    // 평점 남기기 Dialog + 서버 통신
    private fun showRatingDialog(changedRating : Float) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle("평점 남기기")
            .setMessage("평점을 남기시겠습니까?")
            .setPositiveButton("확인") { _, _ ->
                val ratingData = RatingData(changedRating)
                mainViewModel.requestRating(id, ratingData,
                    onSuccess = {
                        if (changedRating == 0f) {
                            Toast.makeText(requireContext(), "등록된 평점이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), "평점이 성공적으로 등록되었습니다.", Toast.LENGTH_SHORT).show()
                        }
                        currentRating = changedRating
                        isRatingCanceled = false
                    },
                    onFailure = {
                        Toast.makeText(requireContext(), "평점 등록에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                        isRatingCanceled = true
                        binding.myRating.rating = currentRating
                    },
                    onErrorAction = {
                        Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
                )
            }
            .setCancelable(false)
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