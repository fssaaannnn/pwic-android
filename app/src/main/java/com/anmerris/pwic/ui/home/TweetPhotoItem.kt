package com.anmerris.pwic.ui.home

import com.anmerris.pwic.data.TweetMedia

class TweetPhotoItem {
    enum class Type {
        HEADER, PHOTO
    }

    val type: Type
    val id: Long
    val header: String
    val tweetMedia: TweetMedia

    constructor(header: String, id: Long, tweetMedia: TweetMedia) {
        type = Type.HEADER
        this.id = id
        this.header = header
        this.tweetMedia = tweetMedia
    }

    constructor(tweetMedia: TweetMedia) {
        type = Type.PHOTO
        id = tweetMedia.tweet.id
        header = ""
        this.tweetMedia = tweetMedia
    }

}
