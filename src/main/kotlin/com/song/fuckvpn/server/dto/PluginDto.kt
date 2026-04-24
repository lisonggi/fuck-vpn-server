package com.song.fuckvpn.server.dto

import com.song.fuckvpn.server.model.PluginInfoModel
import com.song.fuckvpn.server.modules.PluginManager

data class PluginUpdateConfigRequest(val enabled: Boolean?)

data class PluginConfigResponse(
    val enabled: Boolean,
    val configUpdating: Boolean,
    val pluginInfo: PluginInfoModel
) {
    companion object {
        fun fromManager(manager: PluginManager): PluginConfigResponse {
            return PluginConfigResponse(
                manager.getConfig().enabled,
                manager.getConfigUpdating(),
                manager.getInfo()
            )
        }
    }
}

