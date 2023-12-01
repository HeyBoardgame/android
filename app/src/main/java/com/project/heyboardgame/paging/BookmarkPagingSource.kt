package com.project.heyboardgame.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.project.heyboardgame.dataModel.BoardGame
import com.project.heyboardgame.retrofit.Api

class BookmarkPagingSource(private val api: Api, private val sort: String, private val size: Int) : PagingSource<Int, BoardGame>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, BoardGame> {
        val pageNum = params.key ?: 0
        return try {
            val response = api.getBookmarkList(sort, pageNum, size)
            if (response.isSuccessful) {
                val historyResult = response.body()
                val bookmarkList = historyResult?.result?.boardGames ?: emptyList()
                val prevPage = historyResult?.result?.prevPage
                val nextPage = historyResult?.result?.nextPage

                LoadResult.Page(data = bookmarkList, prevKey = prevPage, nextKey = nextPage)

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