package com.project.heyboardgame.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.project.heyboardgame.dataModel.Friend
import com.project.heyboardgame.retrofit.Api

class FriendListPagingSource(private val api: Api, private val size: Int) : PagingSource<Int, Friend>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Friend> {
        val pageNum = params.key ?: 0
        return try {
            val response = api.getFriendList(pageNum, size)
            if (response.isSuccessful) {
                val friendResult = response.body()
                val friendList = friendResult?.result?.friends ?: emptyList()
                val prevPage = friendResult?.result?.prevPage
                val nextPage = friendResult?.result?.nextPage

                LoadResult.Page(data = friendList, prevKey = prevPage, nextKey = nextPage)

            } else {
                LoadResult.Error(Exception("페이징 실패"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Friend>): Int? {
        return state.pages.lastOrNull()?.nextKey
    }
}