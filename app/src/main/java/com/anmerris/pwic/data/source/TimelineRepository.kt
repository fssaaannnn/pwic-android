package com.anmerris.pwic.data.source

import com.anmerris.pwic.data.MediaRecord
import com.anmerris.pwic.data.TweetRecord

class TimelineRepository(
        val timelineRemoteDataSource: TimelineDataSource,
        val timelineLocalDataSource: TimelineDataSource
) : TimelineDataSource {

    override fun loadNewTweets(callback: TimelineDataSource.LoadTimelineCallback) {
        timelineRemoteDataSource.loadNewTweets(object : TimelineDataSource.LoadTimelineCallback {
            override fun onDataNotAvailable() {
                callback.onDataNotAvailable()
            }

            override fun onTimelineLoaded(tweetRecords: List<TweetRecord>, mediaRecords: List<MediaRecord>) {
                timelineLocalDataSource.populateTweets(tweetRecords, mediaRecords, object : TimelineDataSource.ExecutionCallback {
                    override fun onComplete() {
                        callback.onTimelineLoaded(tweetRecords, mediaRecords)
                    }
                })
            }
        })
    }

    override fun loadOldTweets(callback: TimelineDataSource.LoadTimelineCallback) {
        timelineRemoteDataSource.loadOldTweets(object : TimelineDataSource.LoadTimelineCallback {
            override fun onDataNotAvailable() {
                callback.onDataNotAvailable()
            }

            override fun onTimelineLoaded(tweetRecords: List<TweetRecord>, mediaRecords: List<MediaRecord>) {
                timelineLocalDataSource.populateTweets(tweetRecords, mediaRecords, object : TimelineDataSource.ExecutionCallback {
                    override fun onComplete() {
                        callback.onTimelineLoaded(tweetRecords, mediaRecords)
                    }
                })
            }
        })
    }

    override fun loadAllTweetMedia(callback: TimelineDataSource.LoadTweetMediaCallback) {
        timelineLocalDataSource.loadAllTweetMedia(callback)
    }

    override fun populateTweets(tweetRecords: List<TweetRecord>, mediaRecords: List<MediaRecord>, callback: TimelineDataSource.ExecutionCallback) {

    }

    override fun clearAllTweets(callback: TimelineDataSource.ExecutionCallback) {
        timelineLocalDataSource.clearAllTweets(callback)
    }

    companion object {
        private var INSTANCE: TimelineRepository? = null

        fun getInstance(timelineRemoteDataSource: TimelineDataSource,
                        timelineLocalDataSource: TimelineDataSource) =
                INSTANCE ?: synchronized(TimelineRepository::class.java) {
                    INSTANCE
                            ?: TimelineRepository(timelineRemoteDataSource, timelineLocalDataSource)
                                    .also { INSTANCE = it }
                }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}