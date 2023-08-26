package com.project.heyboardgame.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.project.heyboardgame.dataModel.Chat
import com.project.heyboardgame.retrofit.Api

class ChatPagingSource(private val id: Int, private val api: Api, private val size: Int) : PagingSource<Int, Chat>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Chat> {
        val pageNum = params.key ?: 0
        return try {
            val response = api.getChatting(id, pageNum, size)
            if (response.isSuccessful) {
                val chatResult = response.body()
                val chatting = chatResult?.result?.rooms?: emptyList()
                val prevPage = chatResult?.result?.prevPage
                val nextPage = chatResult?.result?.nextPage

                LoadResult.Page(data = chatting, prevKey = prevPage, nextKey = nextPage)

            } else {
                LoadResult.Error(Exception("페이징 실패"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Chat>): Int? {
        return state.pages.lastOrNull()?.nextKey
    }
}