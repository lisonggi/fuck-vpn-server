package com.song.fuckvpn.server.command

import com.song.fuckvpn.server.model.Subscription
import org.openapitools.jackson.nullable.JsonNullable

data class SubscriptionItemCommand(
    val name: String? = null,
    val enabled: Boolean? = null,
    val expireAt: JsonNullable<Long> = JsonNullable.undefined(),
    val usageLimit: JsonNullable<Int> = JsonNullable.undefined(),
) {
    fun toSub(name: String): Subscription {
        val enabled = enabled ?: false
        val expireAtValue = if (expireAt.isPresent) expireAt.orElse(null) else null
        val usageLimitValue = if (usageLimit.isPresent) usageLimit.orElse(null) else null

        return Subscription(
            name = name,
            enabled = enabled,
            expireAt = expireAtValue,
            usageLimit = usageLimitValue
        )
    }
}
