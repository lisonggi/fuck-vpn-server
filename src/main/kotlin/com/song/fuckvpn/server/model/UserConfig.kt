package com.song.fuckvpn.server.model

import kotlinx.serialization.Serializable

@Serializable
data class UserConfig(val username: String, val password: String)
