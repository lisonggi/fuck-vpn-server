package com.song.fuckvpn.server.dto

data class NodeStateDto(
    val configUpdating: Boolean, val generating: Boolean,
    var nextTime: Long?, val nodeTaskConfig: NodeConfigDto
)