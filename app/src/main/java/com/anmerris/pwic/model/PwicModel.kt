package com.anmerris.pwic.model

import com.anmerris.pwic.data.MediaRecord
import com.anmerris.pwic.data.TweetMedia
import com.anmerris.pwic.data.TweetRecord
import com.anmerris.pwic.data.source.TimelineDataSource
import com.anmerris.pwic.data.source.TimelineRepository
import com.twitter.sdk.android.core.TwitterCore


class PwicModel(val timelineRepository: TimelineRepository) {
    var currentUser: User = User()
        private set
    private val userCallbacks: MutableSet<UserCallback> = HashSet()
    var hasMoreNewItem = false
        private set
    var hasMoreOldItem = false
        private set

    fun loadSessions() {
        val sessionManager = TwitterCore.getInstance().sessionManager
        val activeSession = sessionManager.activeSession
        if (activeSession != null) {
            val user = User(activeSession.userId, activeSession.userName)

            switchUser(user)
        }
    }

    fun switchUser(user: User) {
        if (user == currentUser) {
            return
        }

        currentUser = user
        notifyCurrentUserChanged()
    }

    fun hasLoggedInUser(): Boolean {
        return currentUser.isValid
    }

    fun registerUserCallback(userCallback: UserCallback) {
        userCallbacks.add(userCallback)
    }

    fun unregisterUserCallback(userCallback: UserCallback) {
        userCallbacks.remove(userCallback)
    }

    private fun notifyCurrentUserChanged() {
        userCallbacks.forEach({ it -> it.onCurrentUserChanged(currentUser) })
    }

    interface LoadMediaCallback {
        fun onStarted()
        fun onLoaded(media: List<TweetMedia>)
        fun onDataNotAvailable()
    }

    fun loadInitMedia(callback: LoadMediaCallback) {
        timelineRepository.loadAllTweetMedia(object : TimelineDataSource.LoadTweetMediaCallback {
            // local data loaded
            override fun onTweetMediaLoaded(tweetMedia: List<TweetMedia>) {
                callback.onStarted()
                callback.onLoaded(tweetMedia)
            }

            // local data is empty
            override fun onDataNotAvailable() {
                // try to load remote
                loadNewMedia(callback)
            }
        })
    }

    fun loadNewMedia(callback: LoadMediaCallback) {
        callback.onStarted()
        timelineRepository.loadNewTweets(object : TimelineDataSource.LoadTimelineCallback {
            override fun onTimelineLoaded(tweetRecords: List<TweetRecord>, mediaRecords: List<MediaRecord>) {
                hasMoreNewItem = tweetRecords.isNotEmpty()
                hasMoreOldItem = true
                timelineRepository.loadAllTweetMedia(object : TimelineDataSource.LoadTweetMediaCallback {
                    override fun onTweetMediaLoaded(tweetMedia: List<TweetMedia>) {
                        callback.onLoaded(tweetMedia)
                    }

                    override fun onDataNotAvailable() {
                        callback.onLoaded(mutableListOf())
                    }
                })
            }

            override fun onDataNotAvailable() {
                hasMoreNewItem = false;
                callback.onDataNotAvailable()
            }
        })
    }

    fun loadOldMedia(callback: LoadMediaCallback) {
        callback.onStarted()
        timelineRepository.loadOldTweets(object : TimelineDataSource.LoadTimelineCallback {
            override fun onTimelineLoaded(tweetRecords: List<TweetRecord>, mediaRecords: List<MediaRecord>) {
                hasMoreOldItem = tweetRecords.isNotEmpty()
                timelineRepository.loadAllTweetMedia(object : TimelineDataSource.LoadTweetMediaCallback {
                    override fun onTweetMediaLoaded(tweetMedia: List<TweetMedia>) {
                        callback.onLoaded(tweetMedia)
                    }

                    override fun onDataNotAvailable() {
                        callback.onLoaded(mutableListOf())
                    }
                })
            }

            override fun onDataNotAvailable() {
                hasMoreOldItem = false
                callback.onDataNotAvailable()
            }
        })
    }

}
