package com.anmerris.pwic

import android.content.Context
import com.anmerris.pwic.data.source.TimelineRepository
import com.anmerris.pwic.data.source.local.TimelineDatabase
import com.anmerris.pwic.data.source.local.TimelineLocalDataSource
import com.anmerris.pwic.data.source.remote.TimelineRemoteDataSource
import com.anmerris.pwic.model.PwicModel
import com.anmerris.pwic.utils.AppExecutors


object Injection {

    fun provideModel(context: Context): PwicModel {
        val app = context.applicationContext as App
        return app.model
    }

    fun provideTimelineRepository(context: Context): TimelineRepository {
        val database = TimelineDatabase.getInstance(context)
        val appExecutors = AppExecutors()
        return TimelineRepository.getInstance(
                TimelineRemoteDataSource.getInstance(appExecutors, database.timelineDao()),
                TimelineLocalDataSource.getInstance(appExecutors, database.timelineDao())
        )
    }

}
