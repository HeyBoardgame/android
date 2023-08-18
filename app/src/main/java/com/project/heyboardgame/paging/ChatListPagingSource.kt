package com.project.heyboardgame.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.project.heyboardgame.dataModel.ChatRoom
import com.project.heyboardgame.retrofit.Api

class ChatListPagingSource(private val api: Api, private val size: Int) : PagingSource<Int, ChatRoom>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ChatRoom> {
        val pageNum = params.key ?: 0
        return try {
            val response = api.getChatList(pageNum, size)
            if (response.isSuccessful) {
                val chatListResult = response.body()
                val chatList = chatListResult?.result?.rooms?: emptyList()
                val prevPage = chatListResult?.result?.prevPage
                val nextPage = chatListResult?.result?.nextPage

                LoadResult.Page(data = chatList, prevKey = prevPage, nextKey = nextPage)

            } else {
                LoadResult.Error(Exception("페이징 실패"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ChatRoom>): Int? {
        return state.pages.lastOrNull()?.nextKey
    }
}