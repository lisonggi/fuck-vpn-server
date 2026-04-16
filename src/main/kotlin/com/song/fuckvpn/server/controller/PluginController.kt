package com.song.fuckvpn.server.controller

import com.song.fuckvpn.server.service.ServiceLoader
import com.song.fuckvpn.server.common.dto.ResultDto
import com.song.fuckvpn.server.dto.PluginInfoDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/plugin")
@RestController
class PluginController(private val serviceLoader: ServiceLoader) {
    @GetMapping("/getAllPlugin")
    fun getAllPlugin(): ResultDto<List<PluginInfoDto>> {
        return ResultDto("ok", serviceLoader.getAllPlugin())
    }

    @GetMapping("/{id}")
    fun getPlugin(@PathVariable id: String): ResultDto<PluginInfoDto> {
        return ResultDto("ok", serviceLoader.getPlugin(id))
    }
}