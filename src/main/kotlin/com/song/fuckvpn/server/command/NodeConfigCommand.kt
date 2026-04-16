package com.song.fuckvpn.server.command

data class NodeConfigCommand(
    var autoRefresh: Boolean? = null,
    var delayMilliseconds: Long? = null,
)