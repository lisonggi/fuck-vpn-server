package com.song.fuckvpn.server.controller

import com.song.fuckvpn.server.common.dto.ResultDto
import com.song.fuckvpn.server.service.ServiceLoader
import org.springframework.web.bind.annotation.*

@RequestMapping("/{id}")
@RestController
class NodeController(
    private val serviceLoader: ServiceLoader,
) {
    @GetMapping("/getNodes")
    fun getNodes(@PathVariable id: String): ResultDto {
        TODO()
    }

    @PutMapping("/setCycle")
    fun setCycle(@PathVariable id: String, @RequestBody cycle: Long): ResultDto {
        TODO()
    }

    @PostMapping("/refreshNodes")
    fun refreshNodes(@PathVariable id: String): ResultDto {
        TODO()
    }
}