package com.song.fuckvpn.server.controller

import com.song.fuckvpn.server.common.dto.ResultDto
import com.song.fuckvpn.server.service.PluginService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/{id}")
@RestController
class SubController(
    private val pluginService: PluginService,
) {
    @GetMapping("/getsubs")
    fun getSubs(@PathVariable id: String): ResultDto {
        TODO()
    }

    @PutMapping("/updateSub")
    fun updateSub(@PathVariable id: String): ResultDto {
        TODO()
    }

    @DeleteMapping("/removeSub")
    fun removeSub(@PathVariable id: String): ResultDto {
        TODO()
    }

    @PostMapping("/addSub")
    fun addSub(@PathVariable id: String): ResultDto {
        TODO()
    }

    @PostMapping("/useSub")
    fun useSub(@PathVariable id: String, @RequestBody uuid: String): ResultDto {
        TODO()
    }
}