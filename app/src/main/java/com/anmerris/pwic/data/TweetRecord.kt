package com.anmerris.pwic.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "tweets")
data class TweetRecord(@PrimaryKey
                       @ColumnInfo(name = "id")
                       val id: Long,
                       @ColumnInfo(name = "time")
                       val time: Long,
                       @ColumnInfo(name = "is_retweet")
                       val isRetweet: Boolean)