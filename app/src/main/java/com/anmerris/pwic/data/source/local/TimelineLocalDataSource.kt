package com.anmerris.pwic.data.source.local

import android.support.annotation.VisibleForTesting
import com.anmerris.pwic.data.MediaRecord
import com.anmerris.pwic.data.TweetRecord
import com.anmerris.pwic.data.source.TimelineDataSource
import com.anmerris.pwic.utils.AppExecutors

class TimelineLocalDataSource private constructor(
        val appExecutors: AppExecutors,
        val timelineDao: TimelineDao) : TimelineDataSource {


    override fun loadNewTweets(callback: TimelineDataSource.LoadTimelineCallback) {
        appExecutors.diskIo.execute {
            val tweets = timelineDao.readAllTweets()
            val media = timelineDao.readAllMedia()
            appExecutors.mainThread.execute {
                if (tweets.isEmpty()) {
                    callback.onDataNotAvailable()
                } else {
                    callback.onTimelineLoaded(tweets, media)
                }
            }
        }
    }

    override fun loadOldTweets(callback: TimelineDataSource.LoadTimelineCallback) {
        loadNewTweets(callback)
    }

    override fun loadAllTweetMedia(callback: TimelineDataSource.LoadTweetMediaCallback) {
        appExecutors.diskIo.execute {
            val tweetMedia = timelineDao.readAllTweetMedia()
            appExecutors.mainThread.execute {
                if (tweetMedia.isEmpty()) {
                    callback.onDataNotAvailable()
                } else {
                    callback.onTweetMediaLoaded(tweetMedia)
                }
            }
        }
    }

    override fun populateTweets(tweetRecords: List<TweetRecord>, mediaRecords: List<MediaRecord>, callback: TimelineDataSource.ExecutionCallback) {
        appExecutors.diskIo.execute {
            timelineDao.insertTweets(tweetRecords)
            timelineDao.insertMedia(mediaRecords)
            appExecutors.mainThread.execute {
                callback.onComplete()
            }
        }
    }

    override fun clearAllTweets(callback: TimelineDataSource.ExecutionCallback) {
        appExecutors.diskIo.execute {
            timelineDao.deleteAllTweets()
            appExecutors.mainThread.execute {
                callback.onComplete()
            }
        }
    }

    companion object {
        private var INSTANCE: TimelineLocalDataSource? = null

        fun getInstance(appExecutors: AppExecutors, timelineDao: TimelineDao): TimelineLocalDataSource {
            if (INSTANCE == null) {
                synchronized(TimelineLocalDataSource::javaClass) {
                    INSTANCE = TimelineLocalDataSource(appExecutors, timelineDao)
                }
            }
            return INSTANCE!!
        }

        @VisibleForTesting
        fun clearInstance() {
            INSTANCE = null
        }
    }
}