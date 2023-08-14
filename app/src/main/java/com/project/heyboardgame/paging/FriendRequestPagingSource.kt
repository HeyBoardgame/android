package com.project.heyboardgame.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.project.heyboardgame.dataModel.Friend
import com.project.heyboardgame.dataModel.FriendResult
import com.project.heyboardgame.retrofit.Api
import retrofit2.Response

class FriendRequestPagingSource(private val api: Api, private val size: Int) : PagingSource<Int, Friend>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Friend> {
        val pageNum = params.key ?: 0
        return try {
            val response = api.getFriendRequestList(pageNum, size)
            if (response.isSuccessful) {
                val friendResult = response.body()
                val friendRequestList = friendResult?.result?.friends ?: emptyList()
                val prevPage = friendResult?.result?.prevPage
                val nextPage = friendResult?.result?.nextPage

                LoadResult.Page(data = friendRequestList, prevKey = prevPage, nextKey = nextPage)

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