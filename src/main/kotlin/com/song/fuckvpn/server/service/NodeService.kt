package com.song.fuckvpn.server.service

import com.song.fuckvpn.plugin.plugin.NodePlugin
import com.song.fuckvpn.server.dto.ServiceInfo


open class NodeService {
    private val nodePlugin: NodePlugin
    private val serviceInfo: ServiceInfo

    constructor(serviceInfo: ServiceInfo, nodePlugin: NodePlugin) {
        this.nodePlugin = nodePlugin
        this.serviceInfo = serviceInfo
    }

    fun getServiceInfo(): ServiceInfo = serviceInfo
    fun getNodePlugin(): NodePlugin {
        return nodePlugin
    }
}