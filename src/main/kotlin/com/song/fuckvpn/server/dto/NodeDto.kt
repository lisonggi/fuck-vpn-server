package com.song.fuckvpn.server.dto

import com.song.fuckvpn.server.model.NodeConfigModel
import com.song.fuckvpn.server.modules.NodeManager

data class NodeUpdateConfigRequest(
    val autoRefresh: Boolean?,
    val delayMilliseconds: Long?
)

data class NodeConfigResponse(
    val configUpdating: Boolean, val generating: Boolean, val nextTime: Long?, val config: NodeConfigModel
) {
    companion object {
        fun fromManager(manager: NodeManager): NodeConfigResponse {
            return NodeConfigResponse(
                manager.getConfigUpdating(),
                manager.getGenerating(),
                manager.getNextTime(),
                manager.getConfig()
            )
        }
    }
}