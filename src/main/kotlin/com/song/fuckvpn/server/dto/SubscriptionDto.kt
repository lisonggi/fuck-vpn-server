package com.song.fuckvpn.server.dto

import com.song.fuckvpn.server.model.SubscriptionConfigModel
import com.song.fuckvpn.server.model.SubscriptionConfigModel.ItemModel.RecordModel
import org.openapitools.jackson.nullable.JsonNullable

class SubscriptionConfigRequest {
    data class UpdateConfigRequest(val enabled: Boolean?, val sort: JsonNullable<String> = JsonNullable.undefined())
    data class AddItemRequest(
        val name: String?,
        val enabled: Boolean?,
        val expireAt: JsonNullable<Long> = JsonNullable.undefined(),
        val usageLimit: JsonNullable<Int> = JsonNullable.undefined(),
        val sort: JsonNullable<String> = JsonNullable.undefined()
    )

    data class UpdateItemRequest(
        val uuid: String,
        val name: String?,
        val enabled: Boolean?,
        val expireAt: JsonNullable<Long> = JsonNullable.undefined(),
        val usageLimit: JsonNullable<Int> = JsonNullable.undefined(),
        val sort: JsonNullable<String> = JsonNullable.undefined()
    )
}

class SubscriptionResponse {
    data class ConfigResponse(val enabled: Boolean, val sort: String?) {
        companion object {
            fun fromModel(model: SubscriptionConfigModel): ConfigResponse {
                return ConfigResponse(model.enabled, model.sort)
            }
        }
    }

    data class ItemResponse(
        val uuid: String,
        val name: String,
        val enabled: Boolean,
        val expireAt: Long?,
        val usageLimit: Int?,
        val sort: String?
    ) {
        companion object {
            fun fromPair(pair: Pair<String, SubscriptionConfigModel.ItemModel>): ItemResponse {
                val uuid = pair.first
                val item = pair.second
                return ItemResponse(
                    uuid = uuid,
                    name = item.name,
                    enabled = item.enabled,
                    expireAt = item.expireAt,
                    usageLimit = item.usageLimit,
                    sort = item.sort
                )
            }

            fun fromSubMap(subMap: Map<String, SubscriptionConfigModel.ItemModel>): List<ItemResponse> {
                return subMap.map { (key, value) ->
                    ItemResponse(
                        uuid = key,
                        name = value.name,
                        enabled = value.enabled,
                        expireAt = value.expireAt,
                        usageLimit = value.usageLimit,
                        sort = value.sort
                    )
                }
            }
        }

        data class RecordResponse(val uuid: String, val records: List<RecordModel>) {
            companion object {
                fun fromPair(pair: Pair<String, List<SubscriptionConfigModel.ItemModel.RecordModel>>): RecordResponse {
                    return RecordResponse(pair.first, pair.second)
                }
            }
        }
    }
}