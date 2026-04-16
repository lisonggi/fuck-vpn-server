package com.song.fuckvpn.server.model

import com.song.fuckvpn.server.command.NodeConfigCommand
import kotlinx.serialization.Serializable

@Serializable
data class NodeConfig(val autoRefresh: Boolean, val delayMilliseconds: Long) {
    fun copyWith(command: NodeConfigCommand): NodeConfig {
        return NodeConfig(command.autoRefresh ?: this.autoRefresh, command.delayMilliseconds ?: this.delayMilliseconds)
    }
}