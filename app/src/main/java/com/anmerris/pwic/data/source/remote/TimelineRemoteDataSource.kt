package com.anmerris.pwic.data.source.remote

import android.support.annotation.VisibleForTesting
import com.anmerris.pwic.data.MediaRecord
import com.anmerris.pwic.data.TweetRecord
import com.anmerris.pwic.data.source.TimelineDataSource
import com.anmerris.pwic.data.source.local.TimelineDao
import com.anmerris.pwic.utils.AppExecutors
import com.twitter.sdk.android.core.TwitterCore
import java.io.IOException

class TimelineRemoteDataSource private constructor(
        val appExecutors: AppExecutors,
        val timelineDao: TimelineDao) : TimelineDataSource {

    override fun loadNewTweets(callback: TimelineDataSource.LoadTimelineCallback) {
        appExecutors.diskIo.execute {
            val latestId = timelineDao.readLatestTweetId()
            loadHomeTimeline(callback, latestId)
        }
    }

    override fun loadOldTweets(callback: TimelineDataSource.LoadTimelineCallback) {
        appExecutors.diskIo.execute {
            val oldestId = timelineDao.readOldestTweetId()
            loadHomeTimeline(callback, null, oldestId)
        }
    }

    private fun loadHomeTimeline(callback: TimelineDataSource.LoadTimelineCallback, sinceId: Long? = null, untilId: Long? = null) {
        val twitterApiClient = TwitterCore.getInstance().apiClient
        val statusesService = twitterApiClient.statusesService
        val call = statusesService.homeTimeline(200, sinceId, untilId, true, null, false, true)
        appExecutors.networkIo.execute {
            try {
                val response = call.execute()
                if (!response.isSuccessful || response.body() == null || response.errorBody() != null) {
                    appExecutors.mainThread.execute {
                        callback.onDataNotAvailable()
                    }
                    return@execute
                }

                val outTweet: MutableList<TweetRecord> = mutableListOf()
                val outMedia: MutableList<MediaRecord> = mutableListOf()
                val loadedTweets = response.body()
                for (tweet in loadedTweets) {
                    val time = TwitterDate(tweet.createdAt).utcMilliSecond
                    val tr = TweetRecord(tweet.id, time, tweet.retweetedStatus != null)
                    outTweet.add(tr)
                    val tweetMedia = tweet.entities?.media
                    if (tweetMedia != null) {
                        for (media in tweetMedia) {
                            if ("photo".equals(media.type, true)) {
                                outMedia.add(MediaRecord(media.id, tweet.id, media.mediaUrlHttps, media.type))
                            }
                        }
                    }
                }
                appExecutors.mainThread.execute {
                    callback.onTimelineLoaded(outTweet, outMedia)
                }
            } catch (e: IOException) {
                appExecutors.mainThread.execute {
                    callback.onDataNotAvailable()
                }
            }

        }
    }

    override fun loadAllTweetMedia(callback: TimelineDataSource.LoadTweetMediaCallback) {

    }

    override fun populateTweets(tweetRecords: List<TweetRecord>, mediaRecords: List<MediaRecord>, callback: TimelineDataSource.ExecutionCallback) {

    }

    override fun clearAllTweets(callback: TimelineDataSource.ExecutionCallback) {

    }

    companion object {
        private var INSTANCE: TimelineRemoteDataSource? = null

        fun getInstance(appExecutors: AppExecutors, timelineDao: TimelineDao): TimelineRemoteDataSource {
            if (INSTANCE == null) {
                synchronized(TimelineRemoteDataSource::javaClass) {
                    INSTANCE = TimelineRemoteDataSource(appExecutors, timelineDao)
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