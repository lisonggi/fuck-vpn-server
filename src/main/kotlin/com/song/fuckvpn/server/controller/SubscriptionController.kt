package com.song.fuckvpn.server.controller

import com.song.fuckvpn.plugin.api.spec.NodeDataSpec
import com.song.fuckvpn.server.common.dto.ResultDto
import com.song.fuckvpn.server.common.exception.NotExistException
import com.song.fuckvpn.server.dto.SubscriptionConfigRequest
import com.song.fuckvpn.server.dto.SubscriptionResponse
import com.song.fuckvpn.server.service.KeyService
import com.song.fuckvpn.server.service.NodeService
import com.song.fuckvpn.server.service.ServiceLoader
import com.song.fuckvpn.server.util.IpUtil
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@RequestMapping("/{id}")
@RestController
class SubscriptionController(
    private val serviceLoader: ServiceLoader
) {
    fun <T> nodeService(id: String, block: (service: NodeService) -> T): T {
        val service = serviceLoader.getKeyService(id)
        return block(service)
    }

    @GetMapping("/getSubConfig")
    fun getSubConfig(@PathVariable id: String): ResultDto<SubscriptionResponse.ConfigResponse> = nodeService(id) {
        val subManager = it.subscriptionManager()
        val configModel = subManager.getConfig()
        ResultDto("ok", SubscriptionResponse.ConfigResponse.fromModel(configModel))
    }

    @GetMapping("/getSubRecords/{uuid}")
    fun getRecords(
        @PathVariable id: String, @PathVariable uuid: String
    ): ResultDto<SubscriptionResponse.ItemResponse.RecordResponse> = nodeService(id) {
        val subManager = it.subscriptionManager()
        val pair = subManager.getRecords(uuid)
        ResultDto("ok", SubscriptionResponse.ItemResponse.RecordResponse.fromPair(pair))
    }

    @PutMapping("/updateSubConfig")
    fun updateSubConfig(
        @PathVariable id: String, @RequestBody request: SubscriptionConfigRequest.UpdateConfigRequest
    ): ResultDto<SubscriptionResponse.ConfigResponse> = nodeService(id) {
        val subManager = it.subscriptionManager()
        val config = subManager.updateConfig(request)
        ResultDto("ok", SubscriptionResponse.ConfigResponse.fromModel(config))
    }

    @GetMapping("/getSub/{uuid}")
    fun getSub(@PathVariable id: String, @PathVariable uuid: String): ResultDto<SubscriptionResponse.ItemResponse> =
        nodeService(id) {
            val subManager = it.subscriptionManager()
            val pair = subManager.getSub(uuid)
            ResultDto("ok", SubscriptionResponse.ItemResponse.fromPair(pair))
        }

    @GetMapping("/getAllSub")
    fun getAllSub(@PathVariable id: String): ResultDto<List<SubscriptionResponse.ItemResponse>> = nodeService(id) {
        val subManager = it.subscriptionManager()
        val subs = subManager.getAllSub()
        ResultDto("ok", SubscriptionResponse.ItemResponse.fromSubMap(subs))
    }

    @PutMapping("/updateSub")
    fun updateSub(
        @PathVariable id: String, @RequestBody request: SubscriptionConfigRequest.UpdateItemRequest
    ): ResultDto<SubscriptionResponse.ItemResponse> = nodeService(id) {
        val subManager = it.subscriptionManager()
        val pair = subManager.update(request)
        ResultDto("ok", SubscriptionResponse.ItemResponse.fromPair(pair))
    }

    @DeleteMapping("/deleteSub/{uuid}")
    fun deleteSub(@PathVariable id: String, @PathVariable uuid: String): ResultDto<SubscriptionResponse.ItemResponse> =
        nodeService(id) {
            val subManager = it.subscriptionManager()
            val pair = subManager.delete(uuid)
            ResultDto("ok", SubscriptionResponse.ItemResponse.fromPair(pair))
        }

    @PostMapping("/addSub")
    fun addSub(
        @PathVariable id: String, @RequestBody request: SubscriptionConfigRequest.AddItemRequest
    ): ResultDto<SubscriptionResponse.ItemResponse> = nodeService(id) {
        val subManager = it.subscriptionManager()
        val pair = subManager.add(request)
        ResultDto("ok", SubscriptionResponse.ItemResponse.fromPair(pair))
    }

    @GetMapping("/useSub/{uuid}", produces = [MediaType.TEXT_PLAIN_VALUE])
    fun useSub(
        httpRequest: HttpServletRequest,
        @PathVariable id: String,
        @PathVariable uuid: String,
        @RequestParam sort: String?
    ): String = nodeService(id) { service ->
        val nodeManager = service.nodeManager()
        val subManager = service.subscriptionManager()
        try {
            subManager.useSub(
                uuid, IpUtil.getClientIp(httpRequest), httpRequest.getHeader("user-agent")
            ) { config, itemConfig ->
                val nodes = nodeManager.getNodes().toMutableList()
                if (nodes.isNotEmpty()) {
                    val key = (service as? KeyService)?.getKeyManager()?.useKey()

                    val sorts =
                        listOf(sort, itemConfig.sort, config.sort).firstNotNullOfOrNull { it?.trim() }?.split(",")
                            ?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()
                    val result = nodes.sortedWith(compareByDescending<NodeDataSpec> { node ->
                        sorts.sumOf { key ->
                            when {
                                node.name == key -> 100        // 完全匹配
                                node.name.startsWith(key) -> 10 // 前缀匹配
                                node.name.contains(key) -> 1    // 包含匹配
                                else -> 0
                            }
                        }
                    }.thenBy { it.name }).toMutableList()
                    if (config.successTip != null) {
                        val successData = result.first().copy().apply {
                            name = config.successTip
                        }
                        result.addFirst(successData)
                    }
                    val defaultNodeData = result.first().copy().apply {
                        name = "默认"
                    }
                    result.addFirst(defaultNodeData)
                    val joinString = result.joinToString("\n") { node ->
                        val link = key?.toSubscription(node) ?: node.toSubscription()
                        link
                    }
                    return@useSub joinString
                } else {
                    throw NotExistException("没有有效的节点")
                }
            }
        } catch (e: Exception) {
            val strings = mutableListOf<String>()
            strings.add("trojan://password@127.0.0.1:443?security=tls&sni=www.domain.net&allowInsecure=1&type=tcp&headerType=none#默认")
            strings.add("trojan://password@127.0.0.1:443?security=tls&sni=www.domain.net&allowInsecure=1&type=tcp&headerType=none#${e.message}")
            val joinString = strings.joinToString("\n")
            return@nodeService joinString
        }
    }
}