package com.song.fuckvpn.server.service

import com.song.fuckvpn.plugin.api.KeyPlugin
import com.song.fuckvpn.plugin.api.model.KeyData
import com.song.fuckvpn.plugin.api.model.PluginInfo
import com.song.fuckvpn.server.model.KeyConfig
import com.song.fuckvpn.server.modules.KeyManager
import com.song.fuckvpn.server.store.PluginConfig
import kotlinx.coroutines.cancel

class KeyService(pluginInfo: PluginInfo, val keyPlugin: KeyPlugin) : NodeService(pluginInfo, keyPlugin) {
    protected val _keyManager: KeyManager = KeyManager(scope, config.keyConfig ?: KeyConfig(false, 5), ::onGenerateKey)

    val keyManager: KeyManager
        get() = requireRun { _keyManager }

    override fun start() {
        if (stateManager.enabled) {
            nodeManager.reConfig()
            _keyManager.reConfig()
        }
    }

    override fun stop() {
        configStore.save(
            PluginConfig(
                stateManager.enabled,
                _subManager.enabled,
                _subManager._subscriptions,
                _nodeManager.config,
                _keyManager.config
            )
        )
        scope.cancel()
    }

    suspend fun onGenerateKey(): KeyData {
        return keyPlugin.generateKey()
    }
}