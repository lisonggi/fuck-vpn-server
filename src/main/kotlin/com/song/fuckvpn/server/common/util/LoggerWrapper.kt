package com.song.fuckvpn.server.common.util

import org.slf4j.LoggerFactory

class LoggerWrapper(private val msg: String) {
    private val logger = LoggerFactory.getLogger("FuckVPNLogger")
    fun info() = logger.info(msg)
    fun debug() = logger.debug(msg)
    fun warn() = logger.warn(msg)
    fun error() = logger.error(msg)
}

val String.log: LoggerWrapper
    get() = LoggerWrapper(this)