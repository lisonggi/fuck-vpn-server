package com.song.fuckvpn.server.modules

import com.song.fuckvpn.server.common.exception.NotAvailableException
import com.song.fuckvpn.server.common.exception.NotExistException
import com.song.fuckvpn.server.dto.SubscriptionConfigRequest
import com.song.fuckvpn.server.model.SubscriptionConfigModel
import com.song.fuckvpn.server.util.IdUtil
import java.util.*

class SubscriptionManager {
    private var config: SubscriptionConfigModel

    constructor(config: SubscriptionConfigModel) {
        this.config = config
    }

    private val locks = WeakHashMap<String, Any>()
    private val locksMutex = Any()

    fun getLock(uuid: String): Any {
        synchronized(locksMutex) {
            return locks.getOrPut(uuid) { Any() }
        }
    }

    fun <T> tryEnabled(block: () -> T): T {
        getConfig().enabled.takeIf { it } ?: throw NotAvailableException("订阅已关闭")
        return block()
    }

    fun <T> withLock(uuid: String, block: () -> T): T {
        val lock = getLock(uuid)
        return synchronized(lock) {
            block()
        }
    }

    fun updateConfig(request: SubscriptionConfigRequest.UpdateConfigRequest): SubscriptionConfigModel {
        val newConfig = config.fromUpdateConfigRequest(request)
        if (newConfig != config) {
            this.config = newConfig
        }
        return newConfig
    }

    fun getConfig(): SubscriptionConfigModel {
        return config
    }

    fun getAllSub(): Map<String, SubscriptionConfigModel.ItemModel> = tryEnabled {
        config.items
    }

    fun getSub(uuid: String): Pair<String, SubscriptionConfigModel.ItemModel> = tryEnabled {
        val item = config.items[uuid] ?: throw NotExistException("没有这个订阅")
        uuid to item
    }

    fun add(request: SubscriptionConfigRequest.AddItemRequest): Pair<String, SubscriptionConfigModel.ItemModel> =
        tryEnabled {
            val uuid = IdUtil.generateId().replace("-", "")
            val item = SubscriptionConfigModel.ItemModel.fromAddItemRequest(request, uuid.take(6))
            val newConfig = config.copy(
                items = config.items + (uuid to item)
            )
            this.config = newConfig
            uuid to item
        }

    fun delete(uuid: String): Pair<String, SubscriptionConfigModel.ItemModel> =
        tryEnabled {
            withLock(uuid) {
                val item = config.items[uuid] ?: throw NotExistException("没有这个订阅")
                val newConfig = config.copy(
                    items = config.items - uuid
                )
                this.config = newConfig
                uuid to item
            }
        }

    fun update(request: SubscriptionConfigRequest.UpdateItemRequest): Pair<String, SubscriptionConfigModel.ItemModel> =
        tryEnabled {
            withLock(request.uuid) {
                val item = config.items[request.uuid] ?: throw NotExistException("没有这个订阅")
                val newItem = item.fromUpdateItemRequest(request)
                val newConfig = config.copy(
                    items = config.items + (request.uuid to newItem)
                )
                this.config = newConfig
                request.uuid to newItem
            }
        }

    fun <T> useSub(
        uuid: String,
        ip: String,
        userAgent: String,
        block: (config: SubscriptionConfigModel, itemConfig: SubscriptionConfigModel.ItemModel) -> T
    ): T =
        tryEnabled {
            withLock(uuid) {
                val item = config.items[uuid] ?: throw NotExistException("没有这个订阅")
                item.enabled.takeIf { it } ?: throw NotAvailableException("订阅已禁用")
                item.expireAt?.let { if (System.currentTimeMillis() > it) throw NotAvailableException("订阅已过期") }
                item.usageLimit?.let { if (item.records.size >= it) throw NotAvailableException("订阅次数用完了") }
                val newItem = item.copy(
                    records = item.records + SubscriptionConfigModel.ItemModel.RecordModel(
                        ip, System.currentTimeMillis(), userAgent
                    )
                )
                val newConfig = config.copy(
                    items = config.items + (uuid to newItem)
                )
                val result = block(config, newItem)
                this.config = newConfig
                result
            }
        }

    fun getRecords(uuid: String): Pair<String, List<SubscriptionConfigModel.ItemModel.RecordModel>> =
        tryEnabled {
            val records = config.items[uuid]?.records ?: throw NotExistException("没有这个订阅")
            uuid to records
        }
}
