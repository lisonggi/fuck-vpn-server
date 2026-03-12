package com.song.fuckvpn.server.controller

import com.song.fuckvpn.server.common.dto.ResultDto
import com.song.fuckvpn.server.service.PluginService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/plugin")
@RestController
class PluginController(private val pluginService: PluginService) {
    @GetMapping
    fun plugins(): ResultDto {
        return ResultDto("ok", pluginService.getServicesInfo())
    }

    @GetMapping("/{id}")
    fun plugins(@PathVariable id: String): ResultDto {
        return ResultDto("ok", pluginService.getServiceInfo(id))
    }
}