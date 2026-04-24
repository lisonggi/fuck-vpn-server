package com.song.fuckvpn.server.modules

import com.song.fuckvpn.plugin.api.spec.KeyDataSpec
import com.song.fuckvpn.server.common.exception.BusyException
import com.song.fuckvpn.server.common.exception.NotExistException
import com.song.fuckvpn.server.dto.KeyUpdateConfigRequest
import com.song.fuckvpn.server.model.KeyConfigModel
import com.song.fuckvpn.server.util.log
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.PriorityBlockingQueue
import kotlin.math.max
import kotlin.time.Duration.Companion.milliseconds

class KeyManager {
    private val scope: CoroutineScope

    private val onGenerateKey: suspend () -> KeyDataSpec

    private var taskJob: Job? = null

    private val updateConfigMutex = Mutex()
    private val checkKeysMutex = Mutex()

    private var config: KeyConfigModel

    private val queue: PriorityBlockingQueue<KeyDataSpec>

    constructor(scope: CoroutineScope, config: KeyConfigModel, onGenerateKey: suspend () -> KeyDataSpec) {
        this.scope = scope
        this.config = config
        this.onGenerateKey = onGenerateKey
        this.queue =
            PriorityBlockingQueue(config.keySize, compareBy<KeyDataSpec> { it.getExpireAt() ?: Long.MAX_VALUE })
    }

    fun getConfig(): KeyConfigModel = config
    fun getChecking(): Boolean = checkKeysMutex.isLocked
    fun getConfigUpdating(): Boolean = updateConfigMutex.isLocked
    fun getKeys(): List<KeyDataSpec> = queue.toList()

    fun updateConfig(request: KeyUpdateConfigRequest): KeyConfigModel {
        if (!updateConfigMutex.tryLock()) {
            throw BusyException("无法更新密钥任务配置")
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

    private suspend fun checkKeys() {
        checkKeysMutex.withLock {
            //清除已过期的
            withContext(Dispatchers.Default) {
                while (isActive) {
                    val key = queue.peek() ?: break
                    key.getExpireAt()?.let {
                        if (it <= System.currentTimeMillis()) {
                            queue.poll()
                        } else {
                            break
                        }
                    }
                    break
                }
            }
            //计算需要补充的数量
            val differ = config.keySize - queue.size
            if (differ > 0) {
                repeat(differ) {
                    try {
                        //进行补充
                        val key = onGenerateKey()
                        queue.put(key)
                    } catch (e: Exception) {
                        e.message?.log?.info()
                    }
                }
            }
        }
    }

    fun reConfig() {
        cancel()
        if (config.autoFill) {
            runTask()
        }
    }

    private fun runTask() {
        taskJob = scope.launch {
            while (isActive) {
                checkKeys()
                val keyData = queue.peek()
                var delayMillis: Long = 0
                if (keyData == null) {
                    delayMillis = 1000 * 60 * 10
                } else {
                    val expireAt: Long? = keyData.getExpireAt()
                    if (expireAt != null) {
                        delayMillis = max(expireAt - System.currentTimeMillis(), 0)
                    } else {
                        break
                    }
                }
                delay(delayMillis.milliseconds)
            }
        }
    }

    fun cancel() {
        taskJob?.cancel()
        taskJob = null
    }

    fun refreshKeys(): KeyConfigModel {
        cancel()
        queue.clear()
        if (config.autoFill) {
            runTask()
        } else {
            taskJob = scope.launch {
                checkKeys()
            }
        }
        return config
    }

    fun useKey(): KeyDataSpec {
        val key = queue.poll() ?: throw NotExistException("没有有效的密钥")
        reConfig()
        return key
    }
}