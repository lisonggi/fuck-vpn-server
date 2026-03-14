package com.song.fuckvpn.server.service

import com.song.fuckvpn.plugin.api.NodePlugin
import com.song.fuckvpn.server.common.exception.AppException
import com.song.fuckvpn.server.dto.PluginInfo
import com.song.fuckvpn.server.enums.RunState

open class NodeService {
    val nodePlugin: NodePlugin
    val pluginInfo: PluginInfo
    var serviceState: RunState
        private set

    constructor(pluginInfo: PluginInfo, nodePlugin: NodePlugin) {
        this.nodePlugin = nodePlugin
        this.pluginInfo = pluginInfo
        this.serviceState = RunState.off
    }

    fun updateServerState(state: RunState): RunState {
        if (state != RunState.off && state != RunState.on) {
            throw AppException("目标状态不正确")
        } else if (state == this.serviceState) {
            throw AppException("当前已经是目标状态")
        } else if (this.serviceState == RunState.turningOff || this.serviceState == RunState.turningOn) {
            throw AppException("状态还没有切换完成")
        }
        this.serviceState = nextState(state)
        this.serviceState = nextState(this.serviceState)
        return this.serviceState
    }

    private fun nextState(state: RunState): RunState {
        return when (state) {
            RunState.on -> RunState.turningOn
            RunState.turningOn -> RunState.on
            RunState.off -> RunState.turningOff
            RunState.turningOff -> RunState.off
        }
    }
}