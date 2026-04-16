package com.song.fuckvpn.server.controller

import com.song.fuckvpn.server.service.ServiceLoader
import com.song.fuckvpn.server.common.dto.ResultDto
import com.song.fuckvpn.server.dto.StateConfigDto
import org.springframework.web.bind.annotation.*

@RequestMapping("/{id}")
@RestController
class StateController(
    private val serviceLoader: ServiceLoader
) {
    @GetMapping("/getStateConfig")
    fun getStateConfig(@PathVariable id: String): ResultDto<StateConfigDto> {
        val stateManager = serviceLoader.getNodeService(id).stateManager
        return ResultDto("ok", StateConfigDto(stateManager.enabled, stateManager.stateUpdating))
    }

    @PutMapping("/updateStateConfig")
    fun updateStateConfig(
        @PathVariable id: String,
        @RequestBody stateConfigDto: StateConfigDto
    ): ResultDto<StateConfigDto> {
        val stateManager = serviceLoader.getNodeService(id).stateManager
        val enabled = stateManager.updateState(stateConfigDto.enabled)
        return ResultDto("ok", StateConfigDto(enabled, stateManager.stateUpdating))
    }
}