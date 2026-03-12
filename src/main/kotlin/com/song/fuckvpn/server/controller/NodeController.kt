package com.song.fuckvpn.server.controller

import com.song.fuckvpn.server.common.dto.ResultDto
import com.song.fuckvpn.server.service.PluginService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/{id}")
@RestController
class NodeController(
    private val pluginService: PluginService,
) {
    @GetMapping("/getnodes")
    fun getNodes(@PathVariable id: String): ResultDto {
        TODO()
    }

    @PutMapping("/setcycle")
    fun setCycle(@PathVariable id: String, @RequestBody cycle: Long): ResultDto {
        TODO()
    }

    @PutMapping("/refreshnodes")
    fun refreshNodes(@PathVariable id: String): ResultDto {
        TODO()
    }
}