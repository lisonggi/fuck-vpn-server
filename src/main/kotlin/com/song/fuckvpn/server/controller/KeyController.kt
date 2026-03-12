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
class KeyController(
    private val pluginService: PluginService,
) {
    @GetMapping("/getkeys")
    fun getKeys(@PathVariable id: String): ResultDto {
        TODO()
    }

    @PostMapping("/usekey")
    fun useKey(@PathVariable id: String, @RequestBody keyId: String): ResultDto {
        TODO()
    }

    @PutMapping("/refreshkeys")
    fun refreshKeys(@PathVariable id: String): ResultDto {
        TODO()
    }
}