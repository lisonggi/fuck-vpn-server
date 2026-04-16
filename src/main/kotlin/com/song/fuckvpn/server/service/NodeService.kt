package com.song.fuckvpn.server.service

import com.song.fuckvpn.plugin.api.NodePlugin
import com.song.fuckvpn.plugin.api.model.NodeData
import com.song.fuckvpn.plugin.api.model.PluginInfo
import com.song.fuckvpn.server.common.exception.NotAvailableException
import com.song.fuckvpn.server.core.Lifecycle
import com.song.fuckvpn.server.model.NodeConfig
import com.song.fuckvpn.server.modules.NodeManager
import com.song.fuckvpn.server.modules.StateManager
import com.song.fuckvpn.server.modules.SubManager
import com.song.fuckvpn.server.store.ConfigStore
import com.song.fuckvpn.server.store.PluginConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import java.io.File

open class NodeService(
    val pluginInfo: PluginInfo, private val nodePlugin: NodePlugin
) : Lifecycle {
    protected val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    protected val configStore = ConfigStore(File("plugin-configs/${pluginInfo.id}.json"), PluginConfig.serializer(),
        { PluginConfig() })
    protected val config = configStore.load()

    val stateManager = StateManager(config.enabled ?: false, ::onUpdateState)

    protected val _nodeManager: NodeManager =
        NodeManager(scope, config.nodeConfig ?: NodeConfig(false, 1000 * 60 * 10), ::onGenerateNodes)

    val nodeManager: NodeManager
        get() = requireRun { _nodeManager }

    protected val _subManager =
        SubManager(config.subscriptionEnabled ?: false, config.subscriptions?.toMutableMap() ?: mutableMapOf())

    val subManager: SubManager
        get() = requireRun { _subManager }

    override fun start() {
        if (stateManager.enabled) {
            _nodeManager.reConfig()
        }
    }

    override fun stop() {
        configStore.save(
            PluginConfig(
                stateManager.enabled,
                _subManager.enabled,
                _subManager._subscriptions,
                _nodeManager.config
            )
        )
        scope.cancel()
    }

    protected fun <T> requireRun(block: () -> T): T {
        stateManager.enabled.takeIf { it } ?: throw NotAvailableException("服务未开启")
        return block()
    }

    private suspend fun onGenerateNodes(): Set<NodeData> {
        return nodePlugin.generateNodes()
    }

    private fun onUpdateState(enabled: Boolean) {
        if (enabled) {
            _nodeManager.reConfig()
        } else {
            _nodeManager.cancel()
        }
    }
}
