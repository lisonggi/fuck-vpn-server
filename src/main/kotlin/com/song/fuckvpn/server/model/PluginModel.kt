package com.song.fuckvpn.server.model

import com.song.fuckvpn.plugin.api.spec.PluginInfoSpec
import com.song.fuckvpn.server.dto.PluginUpdateConfigRequest
import com.song.fuckvpn.server.enums.ServiceType
import kotlinx.serialization.Serializable

data class PluginInfoModel(
    override val id: String,
    override val name: String,
    override val version: String,
    override val author: String?,
    override val description: String?,
    val serviceType: ServiceType
) : PluginInfoSpec

fun PluginInfoSpec.toModel(serviceType: ServiceType): PluginInfoModel {
    return PluginInfoModel(
        id,
        name,
        version,
        author,
        description,
        serviceType
    )
}

@Serializable
data class PluginConfigModel(val enabled: Boolean) {
    fun fromUpdateConfigRequest(request: PluginUpdateConfigRequest): PluginConfigModel {
        return copy(enabled = request.enabled ?: this.enabled)
    }
}