package com.project.heyboardgame.dataModel

import com.google.gson.annotations.SerializedName

data class DetailResult (
    @SerializedName("result")
    val result: DetailResultData
)

data class DetailResultData (
    @SerializedName("boardGame")
    val boardGameDetail : BoardGameDetail,
    @SerializedName("my")
    val myDetail : MyDetail,
    @SerializedName("like")
    val bestRatingFriend : FriendDetail?,
    @SerializedName("dislike")
    val worstRatingFriend : FriendDetail?
)

data class BoardGameDetail (
    @SerializedName("id")
    val id : Long,
    @SerializedName("name") // 보드게임 이름
    val title : String,
    @SerializedName("description") // 상세 설명
    val description : String,
    @SerializedName("avgRating") // 평점
    val starRating : Double,
    @SerializedName("weight") // 보드게임 난이도
    val difficulty : String,
    @SerializedName("playerMin") // 최소 인원 수
    val playerMin : Int,
    @SerializedName("playerMax") // 최대 인원 수
    val playerMax : Int,
    @SerializedName("imagePath") // 이미지
    val image : String,
    @SerializedName("playTime") // 보드게임 소요시간
    val playTime : Int,
    @SerializedName("isLocalized") // 번역 여부
    val translated : String,
    @SerializedName("mechanism") // 사용 전략
    val strategy : List<String>,
    @SerializedName("genre") // 장르
    val genre : List<String>,
    @SerializedName("theme") // 테마
    val theme : List<String>
)

data class MyDetail (
    @SerializedName("isBookmarked") // 찜하기 여부
    val isBookmarked : Boolean,
    @SerializedName("rating") // 내가 남긴 별점
    val myRating : Double?
)

data class FriendDetail (
    @SerializedName("friendId")
    val friendId : Long,
    @SerializedName("nickname")
    val nickname : String,
    @SerializedName("imagePath") // 이미지
    val image : String,
    @SerializedName("rating")
    val rating : Double
)