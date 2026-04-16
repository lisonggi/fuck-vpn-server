package com.song.fuckvpn.server.util

import kotlinx.serialization.json.Json
import org.openapitools.jackson.nullable.JsonNullable
import org.slf4j.LoggerFactory
import java.security.MessageDigest

fun <T> JsonNullable<T>.getOrKeep(old: T?): T? {
    return if (isPresent) get() else old
}

val json = Json {
    ignoreUnknownKeys = true   // 忽略多余字段
    isLenient = true          // 宽松解析
}

fun String.hash(salt: String): String {
    val md = MessageDigest.getInstance("SHA-256")
    val input = this + salt
    val bytes = md.digest(input.toByteArray())
    return bytes.joinToString("") { "%02x".format(it) }
}


class LoggerWrapper(private val msg: String) {
    private val logger = LoggerFactory.getLogger("FuckVPNLogger")
    fun info() = logger.info(msg)
    fun debug() = logger.debug(msg)
    fun warn() = logger.warn(msg)
    fun error() = logger.error(msg)
}

val String.log: LoggerWrapper
    get() = LoggerWrapper(this)