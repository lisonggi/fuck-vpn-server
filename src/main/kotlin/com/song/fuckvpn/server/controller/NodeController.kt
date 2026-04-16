package com.song.fuckvpn.server.controller

import com.song.fuckvpn.server.command.NodeConfigCommand
import com.song.fuckvpn.server.common.dto.ResultDto
import com.song.fuckvpn.server.dto.NodeConfigDto
import com.song.fuckvpn.server.dto.NodeStateDto
import com.song.fuckvpn.server.service.ServiceLoader
import org.springframework.web.bind.annotation.*

@RequestMapping("/{id}")
@RestController
class NodeController(
    private val serviceLoader: ServiceLoader
) {
    @GetMapping("/getNodeState")
    fun getNodeState(@PathVariable id: String): ResultDto<NodeStateDto> {
        val nodeManager = serviceLoader.getNodeService(id).nodeManager
        return ResultDto(
            "ok",
            NodeStateDto(
                nodeManager.configUpdating, nodeManager.generating, nodeManager.nextTime,
                NodeConfigDto(
                    nodeManager.config.autoRefresh,
                    nodeManager.config.delayMilliseconds
                )
            )
        )
    }

    @GetMapping("/getNodes")
    fun getNodes(@PathVariable id: String): ResultDto<List<String>> {
        val nodeManager = serviceLoader.getNodeService(id).nodeManager
        return ResultDto("ok", nodeManager.nodes.map { it.getViewText() }.toList())
    }

    @PostMapping("/refreshNodes")
    fun refreshNodes(@PathVariable id: String): ResultDto<Unit> {
        val nodeManager = serviceLoader.getNodeService(id).nodeManager
        nodeManager.refreshNodes()
        return ResultDto("ok")
    }

    @PutMapping("/updateNodeConfig")
    fun updateNodeConfig(
        @PathVariable id: String,
        @RequestBody command: NodeConfigCommand
    ): ResultDto<NodeConfigDto> {
        val nodeManager = serviceLoader.getNodeService(id).nodeManager
        val config = nodeManager.updateConfig(nodeManager.config.copyWith(command))
        return ResultDto(
            "ok",
            NodeConfigDto(config.autoRefresh, config.delayMilliseconds)
        )
    }
}