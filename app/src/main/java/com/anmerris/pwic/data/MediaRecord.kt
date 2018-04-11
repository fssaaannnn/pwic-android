package com.anmerris.pwic.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "media", foreignKeys = [(ForeignKey(entity = TweetRecord::class, parentColumns = arrayOf("id"), childColumns = arrayOf("tweet_id"), onDelete = ForeignKey.CASCADE))])
data class MediaRecord(@PrimaryKey
                       @ColumnInfo(name = "media_id")
                       val id: Long,
                       @ColumnInfo(name = "tweet_id")
                       val tweet_id: Long,
                       @ColumnInfo(name = "url")
                       val url: String,
                       @ColumnInfo(name = "type")
                       val type: String)