package com.song.fuckvpn.server.controller

import com.song.fuckvpn.server.common.dto.ResultDto
import com.song.fuckvpn.server.service.PluginService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/{id}")
@RestController
class ServerController(
    private val pluginService: PluginService,
) {
    @GetMapping("/getserverstate")
    fun getServerState(@PathVariable id: String): ResultDto {
        TODO()
    }
    @GetMapping("/setserverstate")
    fun setServerState(@PathVariable id: String): ResultDto {
        TODO()
    }
}