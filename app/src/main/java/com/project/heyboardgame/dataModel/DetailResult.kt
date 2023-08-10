package com.project.heyboardgame.dataModel

import com.google.gson.annotations.SerializedName

data class DetailResult(
    @SerializedName("result")
    val result: DetailResultData
)

data class DetailResultData (
    @SerializedName("starRating") // 평점
    val starRating : Double,
    @SerializedName("genre") // 장르
    val genre : List<String>,
    @SerializedName("theme") // 테마
    val theme : List<String>,
    @SerializedName("imagePath") // 이미지
    val image : String,
    @SerializedName("name") // 보드게임 이름
    val title : String,
    @SerializedName("weight") // 보드게임 난이도
    val difficulty : String,
    @SerializedName("playTime") // 보드게임 소요시간
    val timeRequired : Int,
    @SerializedName("playerMax") // 최대 인원 수
    val playerMax : Int,
    @SerializedName("playerMin") // 최소 인원 수
    val playerMin : Int,
    @SerializedName("description") // 상세 설명
    val description : String,
    @SerializedName("mechanism") // 사용 전략
    val strategy : List<String>,
    @SerializedName("isBookmarked") // 찜하기 여부
    val isBookmarked : Boolean,
    @SerializedName("score") // 내가 남긴 별점
    val myRating : Double,
    @SerializedName("isLocalized") // 번역 여부
    val translated : String
)