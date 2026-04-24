package com.song.fuckvpn.server.store

import com.song.fuckvpn.server.model.KeyConfigModel
import com.song.fuckvpn.server.model.NodeConfigModel
import com.song.fuckvpn.server.model.PluginConfigModel
import com.song.fuckvpn.server.model.SubscriptionConfigModel
import kotlinx.serialization.Serializable

@Serializable
data class PluginConfig(
    val pluginConfig: PluginConfigModel,
    val nodeConfig: NodeConfigModel,
    val keyConfig: KeyConfigModel? = null,
    val subscriptionConfig: SubscriptionConfigModel
)