package com.song.fuckvpn.server.service

import com.song.fuckvpn.plugin.api.spec.KeyDataSpec
import com.song.fuckvpn.plugin.api.spec.KeyPluginSpec
import com.song.fuckvpn.server.model.KeyConfigModel
import com.song.fuckvpn.server.model.PluginInfoModel
import com.song.fuckvpn.server.modules.KeyManager
import com.song.fuckvpn.server.store.PluginConfig
import kotlinx.coroutines.cancel

class KeyService(pluginInfo: PluginInfoModel, val keyPlugin: KeyPluginSpec) : NodeService(pluginInfo, keyPlugin) {

    private val keyManager: KeyManager =
        KeyManager(scope, config.keyConfig ?: KeyConfigModel(false, 5), ::onGenerateKey)


    override fun start() {
        if (pluginManager.getConfig().enabled) {
            nodeManager.reConfig()
            keyManager.reConfig()
        }
    }

    override fun stop() {
        configStore.save(
            PluginConfig(
                pluginManager.getConfig(),
                nodeManager.getConfig(),
                keyManager.getConfig(),
                subscriptionManager.getConfig()
            )
        )
        scope.cancel()
    }

    suspend fun onGenerateKey(): KeyDataSpec {
        return keyPlugin.generateKey()
    }

    fun getKeyManager(): KeyManager = requireRun { keyManager }
}