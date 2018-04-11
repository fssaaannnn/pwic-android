package com.anmerris.pwic.ui.home

import android.arch.lifecycle.Observer
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.*
import com.anmerris.pwic.EndlessRecyclerViewScrollListener
import com.anmerris.pwic.R
import com.anmerris.pwic.databinding.HomeFragmentBinding

const val ITEM_THRESHOLD = 10

class HomeFragment : Fragment() {

    private lateinit var viewDataBinding: HomeFragmentBinding
    private lateinit var listAdapter: PhotoListAdapter
    private lateinit var scrollListener: EndlessRecyclerViewScrollListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        viewDataBinding = DataBindingUtil
                .inflate<HomeFragmentBinding>(inflater, R.layout.home_fragment, container, false).apply {
                    viewmodel = (activity as HomeActivity).obtainViewModel()
                }
        viewDataBinding.setLifecycleOwner(this)
        setHasOptionsMenu(true)
        return viewDataBinding.root
    }

    override fun onResume() {
        super.onResume()
        viewDataBinding.viewmodel?.loadInitItems()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val viewModel = viewDataBinding.viewmodel ?: return super.onOptionsItemSelected(item)
        return when (item.itemId) {
            R.id.menu_refresh -> {
                viewModel.loadNewItems()
                true
            }
            R.id.menu_group_by_hour -> {
                viewModel.toggleGroupByHour()
                item.isChecked = viewModel.groupByHour
                true
            }
            R.id.menu_exclude_retweet -> {
                viewModel.toggleExcludeRetweet()
                item.isChecked = viewModel.excludeRetweet
                true
            }
            else -> false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        if (menu == null) return
        val viewModel = viewDataBinding.viewmodel ?: return
        menu.findItem(R.id.menu_group_by_hour).isChecked = viewModel.groupByHour
        menu.findItem(R.id.menu_exclude_retweet).isChecked = viewModel.excludeRetweet
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRefreshLayout()
        setupListAdapter()
    }

    private fun setupListAdapter() {
        val viewModel = viewDataBinding.viewmodel
        if (viewModel != null) {
            listAdapter = PhotoListAdapter()
            val maxSpan = resources.getInteger(R.integer.item_column_count)
            val gml = GridLayoutManager(context, maxSpan)
            gml.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    val rawType = listAdapter.getItemViewType(position)
                    val type = TweetPhotoItem.Type.values()[rawType]
                    return when (type) {
                        TweetPhotoItem.Type.HEADER -> maxSpan
                        TweetPhotoItem.Type.PHOTO -> 1
                    }
                }
            }
            viewDataBinding.photoList.layoutManager = gml
            viewDataBinding.photoList.adapter = listAdapter
            scrollListener = object : EndlessRecyclerViewScrollListener(gml, ITEM_THRESHOLD) {
                override fun onLoadMore() {
                    viewModel.loadOldItems()
                }
            }
            viewDataBinding.photoList.addOnScrollListener(scrollListener)

            viewModel.items.observe(this, Observer<List<TweetPhotoItem>> { t ->
                if (t != null) {
                    listAdapter.updateContents(t)
                }
            })
            viewModel.hasMoreOld.observe(this, Observer<Boolean> { t ->
                if (t != null) {
                    scrollListener.shouldLoadMore = t
                }
            })
            viewModel.loadingOld.observe(this, Observer<Boolean> { t ->
                if (t != null) {
                    scrollListener.isLoading = t
                }
            })
        } else {
            Log.w(TAG, "ViewModel not initialized when attempting to set up adapter.")
        }
    }

    private fun setupRefreshLayout() {
        viewDataBinding.refreshLayout.run {
            setColorSchemeColors(
                    ContextCompat.getColor(context, R.color.colorPrimary),
                    ContextCompat.getColor(context, R.color.colorAccent),
                    ContextCompat.getColor(context, R.color.colorPrimaryDark)
            )
        }
    }

    companion object {
        fun newInstance() = HomeFragment()
        private const val TAG = "HomeFragment"
    }

}

