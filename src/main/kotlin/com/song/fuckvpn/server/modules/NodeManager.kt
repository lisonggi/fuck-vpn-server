package com.song.fuckvpn.server.modules

import com.song.fuckvpn.plugin.api.spec.NodeDataSpec
import com.song.fuckvpn.server.common.exception.BusyException
import com.song.fuckvpn.server.dto.NodeUpdateConfigRequest
import com.song.fuckvpn.server.model.NodeConfigModel
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration.Companion.milliseconds

class NodeManager {
    private val scope: CoroutineScope

    private val onGenerateNodes: suspend () -> List<NodeDataSpec>

    private var taskJob: Job? = null

    private val updateConfigMutex = Mutex()
    private val generateNodesMutex = Mutex()

    private var config: NodeConfigModel

    private var nextTime: Long? = null

    private var nodes: List<NodeDataSpec> = emptyList()

    constructor(scope: CoroutineScope, config: NodeConfigModel, onGenerateNodes: suspend () -> List<NodeDataSpec>) {
        this.scope = scope
        this.config = config
        this.onGenerateNodes = onGenerateNodes
    }

    fun getConfig(): NodeConfigModel = config
    fun getConfigUpdating(): Boolean = updateConfigMutex.isLocked
    fun getGenerating(): Boolean = generateNodesMutex.isLocked
    fun getNextTime(): Long? = nextTime
    fun getNodes(): List<NodeDataSpec> = nodes

    fun updateConfig(request: NodeUpdateConfigRequest): NodeConfigModel {
        if (!updateConfigMutex.tryLock()) {
            throw BusyException("无法更新节点任务配置")
        }
        try {
            val newConfig = this.config.fromUpdateConfigRequest(request)
            if (newConfig != this.config) {
                this.config = newConfig
                reConfig()
            }
            return newConfig
        } finally {
            updateConfigMutex.unlock()
        }
    }

    private suspend fun generateNodes() {
        generateNodesMutex.withLock {
            nodes = onGenerateNodes()
        }
    }


    fun reConfig() {
        cancel()
        if (config.autoRefresh) {
            runTask()
        }
    }

    private fun runTask() {
        taskJob = scope.launch {
            while (isActive) {
                generateNodes()
                val delay = config.delayMilliseconds
                nextTime = System.currentTimeMillis() + delay
                delay(delay.milliseconds)
            }
        }
    }

    fun cancel() {
        taskJob?.cancel()
        taskJob = null
        nextTime = null
    }

    fun refreshNodes():NodeConfigModel {
        cancel()
        if (config.autoRefresh) {
            runTask()
        } else {
            taskJob = scope.launch {
                generateNodes()
            }
        }
        return config
    }
}