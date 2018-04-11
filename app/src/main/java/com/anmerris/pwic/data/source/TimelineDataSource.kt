package com.anmerris.pwic.data.source

import com.anmerris.pwic.data.MediaRecord
import com.anmerris.pwic.data.TweetMedia
import com.anmerris.pwic.data.TweetRecord

interface TimelineDataSource {

    interface LoadTimelineCallback {
        fun onTimelineLoaded(tweetRecords: List<TweetRecord>, mediaRecords: List<MediaRecord>)
        fun onDataNotAvailable()
    }

    interface LoadTweetMediaCallback {
        fun onTweetMediaLoaded(tweetMedia: List<TweetMedia>)
        fun onDataNotAvailable()
    }

    interface ExecutionCallback {
        fun onComplete()
    }

    fun loadNewTweets(callback: LoadTimelineCallback)
    fun loadOldTweets(callback: LoadTimelineCallback)
    fun loadAllTweetMedia(callback: LoadTweetMediaCallback)
    fun populateTweets(tweetRecords: List<TweetRecord>, mediaRecords: List<MediaRecord>, callback: ExecutionCallback)
    fun clearAllTweets(callback: ExecutionCallback)
}