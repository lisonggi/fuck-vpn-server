package com.song.fuckvpn.server.model

import com.song.fuckvpn.server.dto.NodeUpdateConfigRequest
import kotlinx.serialization.Serializable

@Serializable
data class NodeConfigModel(val autoRefresh: Boolean, val delayMilliseconds: Long) {
    fun fromUpdateConfigRequest(request: NodeUpdateConfigRequest): NodeConfigModel {
        return copy(
            autoRefresh = request.autoRefresh ?: this.autoRefresh,
            delayMilliseconds = request.delayMilliseconds ?: this.delayMilliseconds
        )
    }
}