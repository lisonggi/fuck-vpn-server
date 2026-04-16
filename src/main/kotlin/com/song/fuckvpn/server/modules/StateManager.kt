package com.song.fuckvpn.server.modules

import com.song.fuckvpn.server.common.exception.BusyException
import kotlinx.coroutines.sync.Mutex

class StateManager {
    private val onUpdateState: (enabled: Boolean) -> Unit

    private val lock = Mutex()
    val stateUpdating: Boolean
        get() {
            return lock.isLocked
        }

    var enabled: Boolean
        private set

    fun updateState(enabled: Boolean): Boolean {
        if (!lock.tryLock()) {
            throw BusyException("无法更新服务器状态")
        }
        try {
            if (enabled == this.enabled) {
                return this.enabled
            }
            onUpdateState(enabled)
            this.enabled = enabled
            return this.enabled
        } finally {
            lock.unlock()
        }
    }

    constructor(enabled: Boolean = false, onUpdateState: (enabled: Boolean) -> Unit) {
        this.onUpdateState = onUpdateState
        this.enabled = enabled
    }
}