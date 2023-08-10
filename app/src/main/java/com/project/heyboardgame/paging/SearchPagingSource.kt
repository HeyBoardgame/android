package com.project.heyboardgame.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.project.heyboardgame.dataModel.BoardGame2
import com.project.heyboardgame.retrofit.Api

class SearchPagingSource(private val api: Api, private val keyword: String, private val genreIdList: List<Int>, private val numOfPlayer: Int)
    : PagingSource<Int, BoardGame2>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, BoardGame2> {
        val pageNum = params.key ?: 0
        return try {
            val response = api.requestSearchResult(keyword, genreIdList, numOfPlayer, pageNum)
            if (response.isSuccessful) {
                val searchResult = response.body()
                val searchResultList = searchResult?.result?.boardGames ?: emptyList()
                val nextPage = if (searchResultList.size < 20) null else pageNum + 1

                LoadResult.Page(data = searchResultList, prevKey = null, nextKey = nextPage)

            } else {
                LoadResult.Error(Exception("페이징 실패"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, BoardGame2>): Int? {
        return state.pages.lastOrNull()?.nextKey
    }

}