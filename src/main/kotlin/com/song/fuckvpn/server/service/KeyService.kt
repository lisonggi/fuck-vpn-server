package com.song.fuckvpn.server.service

import com.song.fuckvpn.plugin.api.KeyPlugin
import com.song.fuckvpn.server.common.util.log
import com.song.fuckvpn.server.dto.ServiceInfo

class KeyService : NodeService {
    private val keyPlugin: KeyPlugin

    constructor(serviceInfo: ServiceInfo, keyService: KeyPlugin) : super(serviceInfo, keyService) {
        this.keyPlugin = keyService
        "${"a"}key init".log.info()
    }

    fun getKeyPlugin(): KeyPlugin {
        return keyPlugin
    }
}