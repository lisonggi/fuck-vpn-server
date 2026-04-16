package com.song.fuckvpn.server.controller

import com.song.fuckvpn.plugin.api.model.KeyData
import com.song.fuckvpn.server.command.SubscriptionItemCommand
import com.song.fuckvpn.server.common.dto.ResultDto
import com.song.fuckvpn.server.common.exception.NotExistException
import com.song.fuckvpn.server.common.exception.PluginNotSupportedException
import com.song.fuckvpn.server.dto.SubscriptionConfigDto
import com.song.fuckvpn.server.dto.SubscriptionDto
import com.song.fuckvpn.server.dto.SubscriptionItemDto
import com.song.fuckvpn.server.model.Subscription
import com.song.fuckvpn.server.service.ServiceLoader
import com.song.fuckvpn.server.util.IdUtil
import com.song.fuckvpn.server.util.IpUtil
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@RequestMapping("/{id}")
@RestController
class SubController(
    private val serviceLoader: ServiceLoader
) {
    @GetMapping("/getSubConfig")
    fun getSubConfig(@PathVariable id: String): ResultDto<SubscriptionConfigDto> {
        val subManager = serviceLoader.getNodeService(id).subManager
        return ResultDto("ok", SubscriptionConfigDto(subManager.enabled))
    }

    @GetMapping("/getRecords/{uuid}")
    fun getRecords(@PathVariable id: String, @PathVariable uuid: String): ResultDto<List<Subscription.Record>> {
        val subManager = serviceLoader.getNodeService(id).subManager
        return ResultDto("ok", subManager.getRecords(uuid))
    }

    @PutMapping("/updateSubConfig")
    fun updateSubConfig(
        @PathVariable id: String, @RequestBody subscriptionConfigDto: SubscriptionConfigDto
    ): ResultDto<SubscriptionConfigDto> {
        val subManager = serviceLoader.getNodeService(id).subManager
        subManager.enabled = subscriptionConfigDto.enabled
        return ResultDto("ok", SubscriptionConfigDto(subManager.enabled))
    }

    @GetMapping("/getSub/{uuid}")
    fun getSub(@PathVariable id: String, @PathVariable uuid: String): ResultDto<SubscriptionItemDto> {
        val subManager = serviceLoader.getNodeService(id).subManager
        return ResultDto("ok", SubscriptionItemDto(uuid, subManager.getSub(uuid)))
    }

    @GetMapping("/getAllSub")
    fun getAllSub(@PathVariable id: String): ResultDto<Map<String, SubscriptionDto>> {
        val subManager = serviceLoader.getNodeService(id).subManager
        return ResultDto("ok", subManager.subscriptions)
    }

    @PutMapping("/updateSub/{uuid}")
    fun updateSub(
        @PathVariable id: String, @PathVariable uuid: String, @RequestBody command: SubscriptionItemCommand
    ): ResultDto<SubscriptionItemDto> {
        val subManager = serviceLoader.getNodeService(id).subManager
        return ResultDto("ok", SubscriptionItemDto(uuid, subManager.update(uuid, command)))
    }

    @DeleteMapping("/removeSub/{uuid}")
    fun removeSub(@PathVariable id: String, @PathVariable uuid: String): ResultDto<SubscriptionItemDto> {
        val subManager = serviceLoader.getNodeService(id).subManager
        return ResultDto("ok", SubscriptionItemDto(uuid, subManager.remove(uuid)))
    }

    @PostMapping("/addSub")
    fun addSub(
        @PathVariable id: String, @RequestBody command: SubscriptionItemCommand
    ): ResultDto<SubscriptionItemDto> {
        val subManager = serviceLoader.getNodeService(id).subManager
        val uuid = IdUtil.generateId()
        return ResultDto("ok", SubscriptionItemDto(uuid, subManager.add(uuid, command)))
    }

    @GetMapping("/useSub/{uuid}", produces = [MediaType.TEXT_PLAIN_VALUE])
    fun useSub(
        request: HttpServletRequest, @PathVariable id: String, @PathVariable uuid: String
    ): String {
        val subManager = serviceLoader.getNodeService(id).subManager
        val nodeManager = serviceLoader.getNodeService(id).nodeManager
        return subManager.useSub(IpUtil.getClientIp(request), uuid) {
            val nodes = nodeManager.nodes
            var key: KeyData? = null
            try {
                if (nodes.isNotEmpty()) {
                    val keyManager = serviceLoader.getKeyService(id).keyManager
                    key = keyManager.useKey()
                } else {
                    throw NotExistException("没有有效的节点")
                }
            } catch (_: PluginNotSupportedException) {

            }
            return@useSub nodes.joinToString("\n") {
                it.toSubscription(key)
            }
        }
    }
}