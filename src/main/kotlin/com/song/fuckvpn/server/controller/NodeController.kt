package com.song.fuckvpn.server.controller

import com.song.fuckvpn.server.common.dto.ResultDto
import com.song.fuckvpn.server.dto.NodeConfigResponse
import com.song.fuckvpn.server.dto.NodeUpdateConfigRequest
import com.song.fuckvpn.server.service.NodeService
import com.song.fuckvpn.server.service.ServiceLoader
import org.springframework.web.bind.annotation.*

@RequestMapping("/{id}")
@RestController
class NodeController(
    private val serviceLoader: ServiceLoader
) {
    fun <T> nodeService(id: String, block: (service: NodeService) -> T): T {
        val service = serviceLoader.getKeyService(id)
        return block(service)
    }

    @GetMapping("/getNodeConfig")
    fun getNodeConfig(@PathVariable id: String): ResultDto<NodeConfigResponse> = nodeService(id) {
        val nodeManager = it.nodeManager()
        ResultDto("ok", NodeConfigResponse.fromManager(nodeManager))
    }


    @GetMapping("/getNodes")
    fun getNodes(@PathVariable id: String): ResultDto<List<String>> = nodeService(id) {
        val nodeManager = it.nodeManager()
        ResultDto("ok", nodeManager.getNodes().map { it.getViewText() }.toList())
    }

    @PostMapping("/refreshNodes")
    fun refreshNodes(@PathVariable id: String): ResultDto<NodeConfigResponse> = nodeService(id) {
        val nodeManager = it.nodeManager()
        nodeManager.refreshNodes()
        ResultDto("ok", NodeConfigResponse.fromManager(nodeManager))
    }

    @PutMapping("/updateNodeConfig")
    fun updateNodeConfig(
        @PathVariable id: String, @RequestBody request: NodeUpdateConfigRequest
    ): ResultDto<NodeConfigResponse> = nodeService(id) {
        val nodeManager = it.nodeManager()
        nodeManager.updateConfig(request)
        ResultDto(
            "ok", NodeConfigResponse.fromManager(nodeManager)
        )
    }
}