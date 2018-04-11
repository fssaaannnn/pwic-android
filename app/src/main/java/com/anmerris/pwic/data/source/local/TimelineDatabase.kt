package com.anmerris.pwic.data.source.local

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

import com.anmerris.pwic.data.MediaRecord
import com.anmerris.pwic.data.TweetRecord


@Database(entities = [(TweetRecord::class), (MediaRecord::class)], version = 1)
abstract class TimelineDatabase : RoomDatabase() {

    abstract fun timelineDao(): TimelineDao

    companion object {

        @Volatile
        private var INSTANCE: TimelineDatabase? = null

        fun getInstance(context: Context): TimelineDatabase =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
                }

        private fun buildDatabase(context: Context) =
                Room.databaseBuilder(context.applicationContext,
                        TimelineDatabase::class.java, "PwicTimeline.db")
                        .build()
    }

}
