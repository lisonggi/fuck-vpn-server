package com.song.fuckvpn.server.modules

import com.song.fuckvpn.plugin.api.model.NodeData
import com.song.fuckvpn.server.common.exception.BusyException
import com.song.fuckvpn.server.model.NodeConfig
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration.Companion.milliseconds

class NodeManager {
    private val scope: CoroutineScope

    private val onGenerateNodes: suspend () -> Set<NodeData>

    private var taskJob: Job? = null

    private val updateConfigMutex = Mutex()
    private val generateNodesMutex = Mutex()

    var nodes: Set<NodeData> = emptySet()
        private set
        get() = field.toSet()

    var config: NodeConfig
        private set

    val configUpdating: Boolean
        get() = updateConfigMutex.isLocked
    val generating: Boolean
        get() {
            return generateNodesMutex.isLocked
        }
    var nextTime: Long? = null
        private set

    constructor(scope: CoroutineScope, config: NodeConfig, onGenerateNodes: suspend () -> Set<NodeData>) {
        this.scope = scope
        this.config = config
        this.onGenerateNodes = onGenerateNodes
    }

    fun updateConfig(config: NodeConfig): NodeConfig {
        if (!updateConfigMutex.tryLock()) {
            throw BusyException("无法更新节点任务配置")
        }
        try {
            if (config == this.config) {
                return config
            }
            this.config = config
            reConfig()
            return this.config
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

    fun refreshNodes() {
        cancel()
        if (config.autoRefresh) {
            runTask()
        } else {
            taskJob = scope.launch {
                generateNodes()
            }
        }
    }
}