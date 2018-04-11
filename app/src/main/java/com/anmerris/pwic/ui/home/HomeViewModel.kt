package com.anmerris.pwic.ui.home

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.support.v4.os.ConfigurationCompat
import com.anmerris.pwic.App
import com.anmerris.pwic.data.TweetMedia
import com.anmerris.pwic.model.PwicModel

class HomeViewModel(
        context: Application,
        private val model: PwicModel) : AndroidViewModel(context) {

    private var rawItems: List<TweetMedia> = listOf()
    val items = MutableLiveData<List<TweetPhotoItem>>()
    val loadingNew = MutableLiveData<Boolean>()
    val hasMoreNew = MutableLiveData<Boolean>()
    val loadingOld = MutableLiveData<Boolean>()
    val hasMoreOld = MutableLiveData<Boolean>()
    val empty = MutableLiveData<Boolean>()
    var groupByHour = false
        private set
    var excludeRetweet = false
        private set

    init {
        loadingNew.value = false
        hasMoreNew.value = model.hasMoreNewItem
        loadingOld.value = false
        hasMoreOld.value = model.hasMoreOldItem
        empty.value = false
    }

    fun toggleGroupByHour() {
        groupByHour = !groupByHour
        updateItem(rawItems)
    }

    fun toggleExcludeRetweet() {
        excludeRetweet = !excludeRetweet
        updateItem(rawItems)
    }

    private fun updateItem(media: List<TweetMedia>) {
        val configuration = getApplication<App>().resources.configuration
        val locale = ConfigurationCompat.getLocales(configuration)[0]
        val photoItem = PhotoListBuilder(media).build(groupByHour, excludeRetweet, locale)
        items.value = photoItem
        empty.value = photoItem.isEmpty()
    }

    inner class LoadCallback(
            private val loading: MutableLiveData<Boolean>
    ) : PwicModel.LoadMediaCallback {

        override fun onStarted() {
            loading.value = true
            hasMoreNew.value = model.hasMoreNewItem
            hasMoreOld.value = model.hasMoreOldItem
        }

        override fun onLoaded(media: List<TweetMedia>) {
            loading.value = false

            rawItems = media
            updateItem(rawItems)

            hasMoreNew.value = model.hasMoreNewItem
            hasMoreOld.value = model.hasMoreOldItem
        }

        override fun onDataNotAvailable() {
            loading.value = false

            hasMoreNew.value = model.hasMoreNewItem
            hasMoreOld.value = model.hasMoreOldItem
        }
    }

    fun loadInitItems() {
        model.loadInitMedia(LoadCallback(loadingNew))
    }

    fun loadNewItems() {
        model.loadNewMedia(LoadCallback(loadingNew))
    }

    fun loadOldItems() {
        model.loadOldMedia(LoadCallback(loadingOld))
    }

}