package com.song.fuckvpn.server.store

import com.song.fuckvpn.server.model.KeyConfig
import com.song.fuckvpn.server.model.NodeConfig
import com.song.fuckvpn.server.model.Subscription
import kotlinx.serialization.Serializable

@Serializable
data class PluginConfig(
    var enabled: Boolean? = null,
    var subscriptionEnabled: Boolean? = null,
    var subscriptions: Map<String, Subscription>? = null,
    var nodeConfig: NodeConfig? = null,
    var keyConfig: KeyConfig? = null
)