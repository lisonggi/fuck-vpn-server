package com.song.fuckvpn.server.dto

import com.song.fuckvpn.server.model.KeyConfig

data class KeyStateDto(val configUpdating: Boolean, val checking: Boolean,val keyConfig: KeyConfig)
