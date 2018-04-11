package com.anmerris.pwic

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager

abstract class EndlessRecyclerViewScrollListener constructor(
        private val visibleThreshold: Int,
        private val layoutManager: RecyclerView.LayoutManager)
    : RecyclerView.OnScrollListener() {

    var shouldLoadMore = false
    var isLoading = false

    constructor(layoutManager: LinearLayoutManager, visibleThreshold: Int)
            : this(visibleThreshold, layoutManager)

    constructor(layoutManager: GridLayoutManager, visibleThreshold: Int)
            : this(visibleThreshold * layoutManager.spanCount, layoutManager)

    constructor(layoutManager: StaggeredGridLayoutManager, visibleThreshold: Int)
            : this(visibleThreshold * layoutManager.spanCount, layoutManager)

    private fun getLastVisibleItem(lastVisibleItemPositions: IntArray): Int {
        var maxSize = 0
        for (i in lastVisibleItemPositions.indices) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i]
            } else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i]
            }
        }
        return maxSize
    }

    override fun onScrolled(view: RecyclerView, dx: Int, dy: Int) {
        var lastVisibleItemPosition = 0
        val totalItemCount = layoutManager.itemCount

        if (layoutManager is StaggeredGridLayoutManager) {
            val lastVisibleItemPositions = layoutManager.findLastVisibleItemPositions(null)
            // get maximum element within the list
            lastVisibleItemPosition = getLastVisibleItem(lastVisibleItemPositions)
        } else if (layoutManager is GridLayoutManager) {
            lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
        } else if (layoutManager is LinearLayoutManager) {
            lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
        }

        if (shouldLoadMore
                && !isLoading
                && lastVisibleItemPosition + visibleThreshold > totalItemCount) {
            onLoadMore()
        }
    }

    abstract fun onLoadMore()

}