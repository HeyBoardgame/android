package com.project.heyboardgame.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.project.heyboardgame.dataModel.HistoryResultData
import com.project.heyboardgame.retrofit.Api

class BookmarkPagingSource(private val api: Api, private val sort: String) : PagingSource<Int, HistoryResultData>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, HistoryResultData> {
        val pageNum = params.key ?: 0
        return try {
            val response = api.requestBookmarkList(sort, pageNum)
            if (response.isSuccessful) {
                val bookmarkList = response.body()?.result ?: emptyList()
                val nextPage = if (bookmarkList.size < 15) null else pageNum + 1

                LoadResult.Page(data = bookmarkList, prevKey = null, nextKey = nextPage)
            } else {
                LoadResult.Error(Exception("페이징 실패"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, HistoryResultData>): Int? {
        // 마지막 로드된 페이지의 키를 반환하도록 설정
        return state.pages.lastOrNull()?.nextKey
    }
}