package com.song.fuckvpn.server.controller

import com.song.fuckvpn.server.command.KeyConfigCommand
import com.song.fuckvpn.server.common.dto.ResultDto
import com.song.fuckvpn.server.dto.KeyStateDto
import com.song.fuckvpn.server.model.KeyConfig
import com.song.fuckvpn.server.service.ServiceLoader
import org.springframework.web.bind.annotation.*

@RequestMapping("/{id}")
@RestController
class KeyController(
    private val serviceLoader: ServiceLoader
) {
    @GetMapping("/getKeyState")
    fun getKeyState(@PathVariable id: String): ResultDto<KeyStateDto> {
        val keyManager = serviceLoader.getKeyService(id).keyManager
        return ResultDto("ok", KeyStateDto(keyManager.configUpdating, keyManager.checking, keyManager.config))
    }

    @GetMapping("/getKeys")
    fun getKeys(@PathVariable id: String): ResultDto<List<String>> {
        val service = serviceLoader.getKeyService(id)
        return ResultDto("ok", service.keyManager.keys.map { it.getViewText() }.toList())
    }

    @PostMapping("/useKey")
    fun useKey(@PathVariable id: String): ResultDto<String> {
        val service = serviceLoader.getKeyService(id)
        return ResultDto("ok", service.keyManager.useKey().getViewText())
    }

    @PostMapping("/refreshKeys")
    fun refreshKeys(@PathVariable id: String): ResultDto<Unit> {
        val service = serviceLoader.getKeyService(id)
        service.keyManager.refreshKeys()
        return ResultDto("ok")
    }

    @PutMapping("/updateKeyConfig")
    fun updateCycle(@PathVariable id: String, @RequestBody command: KeyConfigCommand): ResultDto<KeyConfig> {
        val service = serviceLoader.getKeyService(id)
        return ResultDto("ok", service.keyManager.updateConfig(service.keyManager.config.copyWith(command)))
    }
}