package com.project.heyboardgame.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.project.heyboardgame.dataModel.Timeline
import com.project.heyboardgame.retrofit.Api

class TimelinePagingSource(private val api: Api, private val size: Int) : PagingSource<Int, Timeline>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Timeline> {
        val pageNum = params.key ?: 0
        return try {
            val response = api.requestRecommendTimeline(pageNum, size)
            if (response.isSuccessful) {
                val timelineResult = response.body()
                val timelineList = timelineResult?.result?.content ?: emptyList()
                val prevPage = timelineResult?.result?.prevPage
                val nextPage = timelineResult?.result?.nextPage

                LoadResult.Page(data = timelineList, prevKey = prevPage, nextKey = nextPage)

            } else {
                LoadResult.Error(Exception("페이징 실패"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Timeline>): Int? {
        return state.pages.lastOrNull()?.nextKey
    }
}