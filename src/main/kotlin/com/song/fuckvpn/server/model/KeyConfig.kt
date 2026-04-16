package com.song.fuckvpn.server.model

import com.song.fuckvpn.server.command.KeyConfigCommand
import kotlinx.serialization.Serializable

@Serializable
data class KeyConfig(val autoFill: Boolean, val keySize: Int) {
    fun copyWith(command: KeyConfigCommand): KeyConfig {
        return KeyConfig(command.autoFill ?: this.autoFill, command.keySize ?: this.keySize)
    }
}