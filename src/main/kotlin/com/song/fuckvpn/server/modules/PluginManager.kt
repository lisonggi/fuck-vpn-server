package com.song.fuckvpn.server.modules

import com.song.fuckvpn.server.common.exception.BusyException
import com.song.fuckvpn.server.dto.PluginUpdateConfigRequest
import com.song.fuckvpn.server.model.PluginConfigModel
import com.song.fuckvpn.server.model.PluginInfoModel
import kotlinx.coroutines.sync.Mutex

class PluginManager {
    private val onUpdateConfig: (config: PluginConfigModel) -> Unit

    private val lock = Mutex()

    private var config: PluginConfigModel
    private val info: PluginInfoModel

    constructor(config: PluginConfigModel, info: PluginInfoModel, onUpdateConfig: (config: PluginConfigModel) -> Unit) {
        this.onUpdateConfig = onUpdateConfig
        this.config = config
        this.info = info
    }

    fun getConfig(): PluginConfigModel = config
    fun getInfo(): PluginInfoModel = info
    fun getConfigUpdating(): Boolean = lock.isLocked

    fun updateConfig(request: PluginUpdateConfigRequest): PluginConfigModel {
        if (!lock.tryLock()) {
            throw BusyException("无法更新服务器状态")
        }
        try {
            val newConfig = this.config.fromUpdateConfigRequest(request)
            if (newConfig != this.config) {
                this.config = newConfig
                reConfig()
            }
            return newConfig
        } finally {
            lock.unlock()
        }
    }

    fun reConfig() = onUpdateConfig(config)

}