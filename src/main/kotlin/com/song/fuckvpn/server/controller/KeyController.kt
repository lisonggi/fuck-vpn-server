package com.song.fuckvpn.server.controller

import com.song.fuckvpn.server.common.dto.ResultDto
import com.song.fuckvpn.server.service.ServiceLoader
import org.springframework.web.bind.annotation.*

@RequestMapping("/{id}")
@RestController
class KeyController(
    private val serviceLoader: ServiceLoader,
) {
    @GetMapping("/getKeys")
    fun getKeys(@PathVariable id: String): ResultDto {
        TODO()
    }

    @PostMapping("/useKey")
    fun useKey(@PathVariable id: String, @RequestBody keyId: String): ResultDto {
        TODO()
    }

    @PostMapping("/refreshKeys")
    fun refreshKeys(@PathVariable id: String): ResultDto {
        TODO()
    }
}