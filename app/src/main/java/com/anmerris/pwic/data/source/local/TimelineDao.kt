package com.anmerris.pwic.data.source.local

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.anmerris.pwic.data.MediaRecord
import com.anmerris.pwic.data.TweetMedia
import com.anmerris.pwic.data.TweetRecord

@Dao
interface TimelineDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTweets(tweets: List<TweetRecord>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMedia(media: List<MediaRecord>)

    @Query("DELETE FROM tweets")
    fun deleteAllTweets()

    @Query("SELECT * FROM tweets")
    fun readAllTweets(): List<TweetRecord>

    @Query("SELECT * FROM media")
    fun readAllMedia(): List<MediaRecord>

    @Query("SELECT tweets.*, media.* FROM tweets JOIN media ON tweets.id = media.tweet_id ORDER BY tweet_id DESC")
    fun readAllTweetMedia(): List<TweetMedia>

    @Query("SELECT tweets.*, media.* FROM tweets JOIN media ON tweets.id = media.tweet_id WHERE is_retweet != 1 ORDER BY tweet_id DESC")
    fun readAllTweetMediaExcludeRetweet(): List<TweetMedia>

    @Query("SELECT id FROM tweets ORDER BY id DESC LIMIT 1")
    fun readLatestTweetId(): Long?

    @Query("SELECT id FROM tweets ORDER BY id ASC LIMIT 1")
    fun readOldestTweetId(): Long?

}