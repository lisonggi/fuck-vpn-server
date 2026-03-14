package com.song.fuckvpn.server.controller

import com.song.fuckvpn.server.common.dto.ResultDto
import com.song.fuckvpn.server.dto.StatusRequest
import com.song.fuckvpn.server.service.ServiceLoader
import org.springframework.web.bind.annotation.*

@RequestMapping("/{id}")
@RestController
class ServerController(
    private val serviceLoader: ServiceLoader,
) {
    @GetMapping("/getServerState")
    fun getServerState(@PathVariable id: String): ResultDto {
        val nodeService = serviceLoader.getNodeService(id)
        return ResultDto("ok", StatusRequest(nodeService.serviceState))
    }

    @PutMapping("/setServerState")
    fun setServerState(@PathVariable id: String, @RequestBody req: StatusRequest): ResultDto {
        val nodeService = serviceLoader.getNodeService(id)
        val serverState = nodeService.updateServerState(req.state)
        return ResultDto("ok", StatusRequest(serverState))
    }
}