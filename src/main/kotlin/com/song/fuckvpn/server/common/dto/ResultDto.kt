package com.song.fuckvpn.server.common.dto

import kotlinx.serialization.Serializable

@Serializable
data class ResultDto<T>(val message: String, var body: T? = null)