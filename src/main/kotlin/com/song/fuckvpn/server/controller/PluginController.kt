package com.song.fuckvpn.server.controller

import com.song.fuckvpn.server.common.dto.ResultDto
import com.song.fuckvpn.server.dto.PluginConfigResponse
import com.song.fuckvpn.server.dto.PluginUpdateConfigRequest
import com.song.fuckvpn.server.service.NodeService
import com.song.fuckvpn.server.service.ServiceLoader
import org.springframework.web.bind.annotation.*

@RequestMapping("/plugin")
@RestController
class PluginController(private val serviceLoader: ServiceLoader) {

    fun <T> nodeService(id: String, block: (service: NodeService) -> T): T {
        val service = serviceLoader.getNodeService(id)
        return block(service)
    }

    @GetMapping
    fun getAllPluginConfig(): ResultDto<List<PluginConfigResponse>> {
        val services = serviceLoader.getAllService()
        val pluginInfoResponses = services.map {
            val pluginManager = it.pluginManager()
            PluginConfigResponse.fromManager(pluginManager)
        }
        return ResultDto("ok", pluginInfoResponses)
    }

    @GetMapping("/{id}")
    fun getPluginConfig(@PathVariable id: String): ResultDto<PluginConfigResponse> = nodeService(id) {
        val pluginManager = it.pluginManager()
        ResultDto("ok", PluginConfigResponse.fromManager(pluginManager))
    }

    @PutMapping("/{id}")
    fun updatePluginConfig(
        @PathVariable id: String,
        @RequestBody request: PluginUpdateConfigRequest
    ): ResultDto<PluginConfigResponse> = nodeService(id) {
        val pluginManager = it.pluginManager()
        pluginManager.updateConfig(request)
        ResultDto("ok", PluginConfigResponse.fromManager(pluginManager))
    }
}