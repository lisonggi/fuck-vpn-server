package com.song.fuckvpn.server.service

import com.song.fuckvpn.plugin.api.spec.NodeDataSpec
import com.song.fuckvpn.plugin.api.spec.NodePluginSpec
import com.song.fuckvpn.server.common.exception.NotAvailableException
import com.song.fuckvpn.server.core.Lifecycle
import com.song.fuckvpn.server.model.NodeConfigModel
import com.song.fuckvpn.server.model.PluginConfigModel
import com.song.fuckvpn.server.model.PluginInfoModel
import com.song.fuckvpn.server.model.SubscriptionConfigModel
import com.song.fuckvpn.server.modules.NodeManager
import com.song.fuckvpn.server.modules.PluginManager
import com.song.fuckvpn.server.modules.SubscriptionManager
import com.song.fuckvpn.server.store.ConfigStore
import com.song.fuckvpn.server.store.PluginConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

open class NodeService(
    protected val pluginInfo: PluginInfoModel, private val nodePlugin: NodePluginSpec
) : Lifecycle {
    protected val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    protected val configStore = ConfigStore(
        "plugin-configs/${pluginInfo.id}.json", PluginConfig.serializer()
    ) {
        PluginConfig(
            pluginConfig = PluginConfigModel(false),
            nodeConfig = NodeConfigModel(false, 1000 * 60 * 10),
            subscriptionConfig = SubscriptionConfigModel(
                false, sort = null,
                items = emptyMap()
            )
        )
    }
    protected val config = configStore.load()

    protected val pluginManager = PluginManager(config.pluginConfig, pluginInfo, ::onUpdateConfig)

    protected val nodeManager: NodeManager = NodeManager(scope, config.nodeConfig, ::onGenerateNodes)

    protected val subscriptionManager = SubscriptionManager(config.subscriptionConfig)

    override fun start() {
        if (pluginManager.getConfig().enabled) {
            nodeManager.reConfig()
        }
    }

    override fun stop() {
        configStore.save(
            PluginConfig(
                pluginManager.getConfig(), nodeManager.getConfig(), null, subscriptionManager.getConfig()
            )
        )
        scope.cancel()
    }

    protected fun <T> requireRun(block: () -> T): T {
        pluginManager.getConfig().enabled.takeIf { it } ?: throw NotAvailableException("服务未开启")
        return block()
    }

    private suspend fun onGenerateNodes(): List<NodeDataSpec> {
        return nodePlugin.generateNodes()
    }

    private fun onUpdateConfig(config: PluginConfigModel) {
        if (config.enabled) {
            nodeManager.reConfig()
        } else {
            nodeManager.cancel()
        }
    }

    fun pluginManager(): PluginManager = this.pluginManager
    fun nodeManager(): NodeManager = requireRun { this.nodeManager }
    fun subscriptionManager(): SubscriptionManager = requireRun { this.subscriptionManager }
}