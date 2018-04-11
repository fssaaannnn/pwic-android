package com.anmerris.pwic.utils

import java.util.concurrent.Executor
import java.util.concurrent.Executors

const val NET_THREAD_COUNT = 3

open class AppExecutors constructor(
        val diskIo: Executor = DiskIoThreadExecutor(),
        val networkIo: Executor = Executors.newFixedThreadPool(NET_THREAD_COUNT),
        val mainThread: Executor = MainThreadExecutor()
)