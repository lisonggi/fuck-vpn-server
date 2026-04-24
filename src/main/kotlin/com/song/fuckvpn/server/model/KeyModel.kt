package com.song.fuckvpn.server.model

import com.song.fuckvpn.server.dto.KeyUpdateConfigRequest
import kotlinx.serialization.Serializable

@Serializable
data class KeyConfigModel(val autoFill: Boolean, val keySize: Int) {
    fun fromUpdateConfigRequest(request: KeyUpdateConfigRequest): KeyConfigModel =
        copy(autoFill = request.autoFill ?: this.autoFill, keySize = request.keySize ?: this.keySize)
}