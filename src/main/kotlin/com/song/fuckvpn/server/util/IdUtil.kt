package com.song.fuckvpn.server.util

import java.util.*

object IdUtil {
    fun generateId(): String = UUID.randomUUID().toString()
//        UUID.randomUUID().toString().replace("-", "")
}