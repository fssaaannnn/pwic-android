package com.anmerris.pwic.data.source.remote

import org.junit.Assert.assertEquals
import org.junit.Test

class TwitterDateTest {

    @Test
    fun getUtcMilliSecond() {
        val td = TwitterDate("Tue Aug 28 21:16:23 +0000 2012")
        assertEquals(1346188583000L, td.utcMilliSecond)
    }
}