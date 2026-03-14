package com.song.fuckvpn.server.controller

import com.song.fuckvpn.server.common.dto.ResultDto
import com.song.fuckvpn.server.service.ServiceLoader
import org.springframework.web.bind.annotation.*

@RequestMapping("/{id}")
@RestController
class SubController(
    private val serviceLoader: ServiceLoader,
) {
    @GetMapping("/getSubs")
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