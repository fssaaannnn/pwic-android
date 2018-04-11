package com.anmerris.pwic

import android.app.Application
import android.util.Log
import com.anmerris.pwic.model.PwicModel
import com.twitter.sdk.android.core.DefaultLogger
import com.twitter.sdk.android.core.Twitter
import com.twitter.sdk.android.core.TwitterConfig


class App : Application() {
    lateinit var model: PwicModel

    override fun onCreate() {
        super.onCreate()
        val config = TwitterConfig.Builder(this)
                .logger(DefaultLogger(Log.DEBUG))
                .debug(BuildConfig.DEBUG)
                .build()
        Twitter.initialize(config)
        model = PwicModel(Injection.provideTimelineRepository(this))
        model.loadSessions()
    }

}
