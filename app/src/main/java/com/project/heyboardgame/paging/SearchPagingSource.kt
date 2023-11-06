package com.project.heyboardgame.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.project.heyboardgame.dataModel.BoardGame2
import com.project.heyboardgame.retrofit.Api

class SearchPagingSource(private val api: Api, private val keyword: String, private val genreIdList: List<Long>,
                         private val numOfPlayer: Int, private val size: Int) : PagingSource<Int, BoardGame2>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, BoardGame2> {
        val pageNum = params.key ?: 0
        return try {
            val response = api.getSearchResult(keyword, genreIdList, numOfPlayer, pageNum, size)
            if (response.isSuccessful) {
                val searchResult = response.body()
                val searchResultList = searchResult?.result?.boardGames ?: emptyList()
                val prevPage = searchResult?.result?.prevPage
                val nextPage = searchResult?.result?.nextPage

                LoadResult.Page(data = searchResultList, prevKey = prevPage, nextKey = nextPage)

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