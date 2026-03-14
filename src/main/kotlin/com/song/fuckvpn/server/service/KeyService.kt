package com.song.fuckvpn.server.service

import com.song.fuckvpn.plugin.api.KeyPlugin
import com.song.fuckvpn.server.common.util.log
import com.song.fuckvpn.server.dto.PluginInfo

class KeyService : NodeService {
    private val keyPlugin: KeyPlugin

    constructor(pluginInfo: PluginInfo, keyService: KeyPlugin) : super(pluginInfo, keyService) {
        this.keyPlugin = keyService
    }

    fun getKeyPlugin(): KeyPlugin {
        return keyPlugin
    }
}