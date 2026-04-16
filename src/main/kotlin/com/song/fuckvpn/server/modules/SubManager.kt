package com.song.fuckvpn.server.modules

import com.song.fuckvpn.server.command.SubscriptionItemCommand
import com.song.fuckvpn.server.common.exception.NotAvailableException
import com.song.fuckvpn.server.common.exception.NotExistException
import com.song.fuckvpn.server.dto.SubscriptionDto
import com.song.fuckvpn.server.model.Subscription
import java.util.*

class SubManager {
    var _subscriptions: MutableMap<String, Subscription>

    val subscriptions: Map<String, SubscriptionDto>
        get() = tryEnabled { _subscriptions.mapValues { it.value.toDto() }.toMutableMap() }

    var enabled: Boolean = false

    constructor(enabled: Boolean, subscriptions: MutableMap<String, Subscription>) {
        this.enabled = enabled
        this._subscriptions = subscriptions
    }

    fun add(uuid: String, command: SubscriptionItemCommand): SubscriptionDto = tryEnabled {
        val sub = command.toSub(command.name ?: uuid.take(6))
        this._subscriptions[uuid] = sub
        sub.toDto()
    }

    fun remove(uuid: String): SubscriptionDto = tryEnabled {
        val subscription: Subscription = this._subscriptions.remove(uuid) ?: throw NotExistException("没有这个订阅")
        subscription.toDto()
    }

    fun update(uuid: String, command: SubscriptionItemCommand): SubscriptionDto = tryEnabled {
        val subscription: Subscription = this._subscriptions.computeIfPresent(uuid) { _, old ->
            old.copyWith(command)
        } ?: throw NotExistException("没有这个订阅")
        subscription.toDto()
    }

    private val locks = WeakHashMap<String, Any>()
    private val locksMutex = Any()
    fun getLock(uuid: String): Any {
        synchronized(locksMutex) {
            return locks.getOrPut(uuid) { Any() }
        }
    }

    fun <T> useSub(ip: String, uuid: String, block: () -> T): T = tryEnabled {
        val lock = getLock(uuid)
        val result = synchronized(lock) {
            val subscription = _subscriptions[uuid] ?: throw NotExistException("没有这个订阅")
            subscription.enabled.takeIf { it } ?: throw NotAvailableException("订阅已禁用")
            subscription.expireAt?.let { if (System.currentTimeMillis() > it) throw NotAvailableException("订阅已过期") }
            subscription.usageLimit?.let { if (subscription.records.size >= it) throw NotAvailableException("订阅次数用完了") }
            val result = block()
            subscription.records.add(Subscription.Record(ip))
            result
        }
        return@tryEnabled result
    }

    fun getSub(uuid: String): SubscriptionDto = tryEnabled {
        val subscription: Subscription = _subscriptions[uuid] ?: throw NotExistException("没有这个订阅")
        subscription.toDto()
    }

    fun getRecords(uuid: String): List<Subscription.Record> {
        val subscription: Subscription = _subscriptions[uuid] ?: throw NotExistException("没有这个订阅")
        return subscription.records.toList()
    }

    fun <T> tryEnabled(block: () -> T): T {
        enabled.takeIf { it } ?: throw NotAvailableException("订阅已关闭")
        return block()
    }
}

