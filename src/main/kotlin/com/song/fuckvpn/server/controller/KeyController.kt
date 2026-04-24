package com.song.fuckvpn.server.controller

import com.song.fuckvpn.server.common.dto.ResultDto
import com.song.fuckvpn.server.dto.KeyConfigResponse
import com.song.fuckvpn.server.dto.KeyUpdateConfigRequest
import com.song.fuckvpn.server.service.KeyService
import com.song.fuckvpn.server.service.ServiceLoader
import org.springframework.web.bind.annotation.*

@RequestMapping("/{id}")
@RestController
class KeyController(
    private val serviceLoader: ServiceLoader
) {
    fun <T> keyService(id: String, block: (service: KeyService) -> T): T {
        val service = serviceLoader.getKeyService(id)
        return block(service)
    }

    @GetMapping("/getKeyConfig")
    fun getKeyConfig(@PathVariable id: String): ResultDto<KeyConfigResponse> = keyService(id) {
        val keyManager = it.getKeyManager()
        ResultDto(
            "ok", KeyConfigResponse.fromManager(keyManager)
        )
    }


    @GetMapping("/getKeys")
    fun getKeys(@PathVariable id: String): ResultDto<List<String>> = keyService(id) {
        val keyManager = it.getKeyManager()
        ResultDto("ok", keyManager.getKeys().map { it.getViewText() }.toList())
    }


    @PostMapping("/useKey")
    fun useKey(@PathVariable id: String): ResultDto<String> = keyService(id) {
        val keyManager = it.getKeyManager()
        val nodeManager = it.nodeManager()

        val key = keyManager.useKey()
        val subscriptionLinks = nodeManager.getNodes().map { node ->
            key.toSubscription(node)
        }.joinToString("\n") { it }
        ResultDto("ok", subscriptionLinks)
    }

    @PostMapping("/refreshKeys")
    fun refreshKeys(@PathVariable id: String): ResultDto<KeyConfigResponse> = keyService(id) {
        val keyManager = it.getKeyManager()
        keyManager.refreshKeys()
        ResultDto("ok", KeyConfigResponse.fromManager(keyManager))
    }


    @PutMapping("/updateKeyConfig")
    fun updateCycle(
        @PathVariable id: String,
        @RequestBody request: KeyUpdateConfigRequest
    ): ResultDto<KeyConfigResponse> = keyService(id) {
        val keyManager = it.getKeyManager()
        keyManager.updateConfig(request)
        ResultDto("ok", KeyConfigResponse.fromManager(keyManager))
    }
}