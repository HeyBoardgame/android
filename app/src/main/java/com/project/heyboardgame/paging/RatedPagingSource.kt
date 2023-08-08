package com.project.heyboardgame.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.project.heyboardgame.dataModel.BoardGame
import com.project.heyboardgame.retrofit.Api

class RatedPagingSource(private val api: Api, private val score: Float, private val sort: String) : PagingSource<Int, BoardGame>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, BoardGame> {
        val pageNum = params.key ?: 0
        return try {
            val response = api.requestSpecificRatedList(score, sort, pageNum)
            if (response.isSuccessful) {
                val historyResult = response.body()
                val ratedList = historyResult?.result?.boardGames ?: emptyList()
                val nextPage = if (ratedList.size < 20) null else pageNum + 1

                LoadResult.Page(data = ratedList, prevKey = null, nextKey = nextPage)

            } else {
                LoadResult.Error(Exception("페이징 실패"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, BoardGame>): Int? {
        return state.pages.lastOrNull()?.nextKey
    }
}