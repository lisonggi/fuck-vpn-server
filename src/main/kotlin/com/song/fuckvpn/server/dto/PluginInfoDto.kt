package com.song.fuckvpn.server.dto

import com.song.fuckvpn.plugin.api.model.PluginInfo

data class PluginInfoDto(
    val enabled: Boolean,
    val keyService: Boolean,
    val info: PluginInfo,
)