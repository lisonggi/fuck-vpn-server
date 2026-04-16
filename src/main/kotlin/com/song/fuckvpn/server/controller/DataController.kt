package com.song.fuckvpn.server.controller

import com.song.fuckvpn.server.service.ServiceLoader
import com.song.fuckvpn.server.common.dto.ResultDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/{id}")
@RestController
class DataController(
    private val serviceLoader: ServiceLoader
) {
    @GetMapping("/getData")
    fun getData(@PathVariable id: String): ResultDto<Unit> {
        TODO()
    }
}