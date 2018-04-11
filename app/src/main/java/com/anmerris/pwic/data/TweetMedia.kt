package com.anmerris.pwic.data

import android.arch.persistence.room.Embedded

data class TweetMedia(@Embedded val tweet: TweetRecord,
                      @Embedded val media: MediaRecord)