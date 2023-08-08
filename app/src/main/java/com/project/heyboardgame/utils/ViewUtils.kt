package com.project.heyboardgame.utils

import android.view.View
import androidx.constraintlayout.widget.Group
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState

class ViewUtils {
    companion object {
        fun setNoContentListener(loadStates: CombinedLoadStates,
                                 noContentView: Group,
                                 itemCount: Int) {
            if ( loadStates.source.refresh is LoadState.NotLoading
                && loadStates.append.endOfPaginationReached
                && itemCount < 1
            ) noContentView.visibility = View.VISIBLE
            else
                noContentView.visibility = View.INVISIBLE
        }
    }
}