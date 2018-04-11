package com.anmerris.pwic.data.source.remote

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class TwitterDate(private val createdAt: String) {

    val utcMilliSecond: Long
        get() {
            return try {
                dateFormat.parse(createdAt).time
            } catch (e: ParseException) {
                0L
            }
        }

    companion object {
        private val dateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy", Locale.US)

        init {
            dateFormat.isLenient = true
        }
    }
}
