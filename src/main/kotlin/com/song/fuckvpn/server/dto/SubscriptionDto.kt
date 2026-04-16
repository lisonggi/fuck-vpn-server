package com.song.fuckvpn.server.dto

data class SubscriptionDto(
    var name: String,
    var enabled: Boolean,
    var expireAt: Long? = null,
    var usageLimit: Int? = null,
)