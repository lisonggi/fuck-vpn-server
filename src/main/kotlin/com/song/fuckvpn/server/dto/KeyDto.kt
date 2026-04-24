package com.song.fuckvpn.server.dto

import com.song.fuckvpn.server.model.KeyConfigModel
import com.song.fuckvpn.server.modules.KeyManager

data class KeyUpdateConfigRequest(
    val autoFill: Boolean?,
    val keySize: Int?
)

data class KeyConfigResponse(val configUpdating: Boolean, val checking: Boolean, val config: KeyConfigModel){
    companion object{
        fun fromManager(manager: KeyManager):KeyConfigResponse{
            return KeyConfigResponse(manager.getConfigUpdating(), manager.getChecking(), manager.getConfig())
        }
    }
}