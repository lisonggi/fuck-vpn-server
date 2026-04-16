package com.song.fuckvpn.server.model

import com.song.fuckvpn.server.command.SubscriptionItemCommand
import com.song.fuckvpn.server.dto.SubscriptionDto
import com.song.fuckvpn.server.util.getOrKeep
import kotlinx.serialization.Serializable

@Serializable
data class Subscription(
    var name: String, //名称
    var enabled: Boolean, //是否启用
    var expireAt: Long? = null, //有效期
    var usageLimit: Int? = null, //最大使用次数
    val records: MutableList<Record> = mutableListOf(),
) {
    fun copyWith(command: SubscriptionItemCommand): Subscription {
        return Subscription(
            command.name ?: name,
            command.enabled ?: enabled,
            command.expireAt.getOrKeep(expireAt),
            command.usageLimit.getOrKeep(usageLimit),
            records
        )
    }

    fun deepCopy(): Subscription {
        return this.copy(
            records = this.records.map { it.copy() }.toMutableList()
        )
    }
    fun toDto(): SubscriptionDto {
        return SubscriptionDto(this.name,this.enabled, this.expireAt,this.usageLimit)
    }
    @Serializable
    data class Record(val ip: String, val time: Long = System.currentTimeMillis())
}