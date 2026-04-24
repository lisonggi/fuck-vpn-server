package com.song.fuckvpn.server.model

import com.song.fuckvpn.server.dto.SubscriptionConfigRequest
import kotlinx.serialization.Serializable

@Serializable
data class SubscriptionConfigModel(val enabled: Boolean, val sort: String?, val items: Map<String, ItemModel>) {
    fun fromUpdateConfigRequest(request: SubscriptionConfigRequest.UpdateConfigRequest): SubscriptionConfigModel {
        val sort = if (request.sort.isPresent) request.sort.orElse(null) else this.sort
        return copy(enabled = request.enabled ?: this.enabled, sort = sort)
    }

    @Serializable
    data class ItemModel(
        val name: String,
        val enabled: Boolean,
        val expireAt: Long?,
        val usageLimit: Int?,
        val sort: String?,
        val records: List<RecordModel>
    ) {
        companion object {
            fun fromAddItemRequest(
                request: SubscriptionConfigRequest.AddItemRequest,
                name: String
            ): ItemModel {
                val name = request.name ?: name
                val enabled = request.enabled ?: false
                val expireAt = request.expireAt.orElse(null)
                val usageLimit =
                    request.usageLimit.orElse(null)
                val sort =
                    request.sort.orElse(null)
                val records = emptyList<RecordModel>()
                return ItemModel(name, enabled, expireAt, usageLimit, sort, records)
            }
        }

        fun fromUpdateItemRequest(request: SubscriptionConfigRequest.UpdateItemRequest): ItemModel {
            val expireAt =
                if (request.expireAt.isPresent) request.expireAt.orElse(null) else this.expireAt

            val usageLimit =
                if (request.usageLimit.isPresent) request.usageLimit.orElse(null) else this.usageLimit

            val sort =
                if (request.sort.isPresent) request.sort.orElse(null) else this.sort

            return copy(
                name = request.name ?: this.name,
                enabled = request.enabled ?: this.enabled,
                expireAt = expireAt,
                usageLimit = usageLimit,
                sort = sort
            )
        }

        @Serializable
        data class RecordModel(val ip: String, val time: Long, val userAgent: String)
    }
}