package com.anmerris.pwic.ui.home

import com.anmerris.pwic.data.TweetMedia
import org.apache.commons.lang3.time.DateUtils
import java.text.DateFormat
import java.util.*

class PhotoListBuilder(private val originList: List<TweetMedia>) {

    fun build(spanInHour: Boolean, excludeRetweet: Boolean, locale: Locale): List<TweetPhotoItem> {
        val out: MutableList<TweetPhotoItem> = mutableListOf()

        if (originList.isEmpty()) {
            return out
        }

        val dateFormat: DateFormat
        val truncateField: Int
        if (spanInHour) {
            dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT, locale)
            truncateField = Calendar.HOUR_OF_DAY
        } else {
            dateFormat = DateFormat.getDateInstance(DateFormat.LONG, locale)
            truncateField = Calendar.DAY_OF_MONTH
        }

        var date = Date(0L)
        for (tm in originList) {
            if (excludeRetweet && tm.tweet.isRetweet) {
                continue
            }

            val truncatedDate = DateUtils.truncate(Date(tm.tweet.time), truncateField)
            if (truncatedDate.time != date.time) {
                val header = dateFormat.format(truncatedDate)
                val headerId = truncatedDate.time
                out.add(TweetPhotoItem(header, headerId, tm))
                date = truncatedDate
            }
            out.add(TweetPhotoItem(tm))
        }

        return out
    }

}
