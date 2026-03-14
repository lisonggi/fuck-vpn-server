package com.song.fuckvpn.server.dto

import com.song.fuckvpn.server.enums.ServiceType

data class PluginInfo(val id: String, val name: String, val type: ServiceType)