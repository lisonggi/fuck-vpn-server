package com.song.fuckvpn.server.service

import com.song.fuckvpn.plugin.api.KeyPlugin
import com.song.fuckvpn.plugin.api.NodePlugin
import com.song.fuckvpn.plugin.api.annotation.VPNPlugin
import com.song.fuckvpn.server.common.exception.PluginNotSupportedException
import com.song.fuckvpn.server.dto.PluginInfoDto
import com.song.fuckvpn.server.util.DefaultPath
import com.song.fuckvpn.server.util.log
import io.github.classgraph.ClassGraph
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.stereotype.Service
import java.io.File
import java.net.URLClassLoader

@Service
class ServiceLoader {
    private val serviceMap: Map<String, NodeService>

    constructor() {
        val plugins = mutableMapOf<String, NodeService>()
        val pluginDir = File("${DefaultPath}/plugins")
        pluginDir.absolutePath.log.info()
        val urls = pluginDir.listFiles { f -> f.extension == "jar" }?.map { it.toURI().toURL() }?.toTypedArray()
            ?: emptyArray()
        val classLoader = this.javaClass.classLoader
        val pluginClassLoader = URLClassLoader(urls, classLoader)
        val scanResult = ClassGraph()
            .enableClassInfo()
            .enableAnnotationInfo()
            .overrideClassLoaders(pluginClassLoader)
            .scan().getClassesWithAnnotation(VPNPlugin::class.java)
        scanResult.forEach { classInfo ->
            val clazz = classInfo.loadClass()
            val instance = clazz.getDeclaredConstructor().newInstance()
            if (NodePlugin::class.java.isAssignableFrom(clazz)) {
                val nodePlugin: NodePlugin = instance as NodePlugin
                val pluginInfo = nodePlugin.getPluginInfo()
                val isKeyService: Boolean = KeyPlugin::class.java.isAssignableFrom(clazz)
                if (isKeyService) {
                    val keyPlugin: KeyPlugin = instance as KeyPlugin
                    plugins[pluginInfo.id] = KeyService(pluginInfo, keyPlugin)
                } else {
                    plugins[pluginInfo.id] = NodeService(pluginInfo, nodePlugin)
                }
                "已经装载 ${if (isKeyService) "KeyService" else "NodeService"}: $pluginInfo".log.info()
            }
        }
        this.serviceMap = plugins.toMap()
    }

    fun getAllPlugin(): List<PluginInfoDto> {
        val pluginInfoDtoDtoList = mutableListOf<PluginInfoDto>()
        serviceMap.forEach { (id) ->
            pluginInfoDtoDtoList.add(getPlugin(id))
        }
        return pluginInfoDtoDtoList
    }

    fun getPlugin(id: String): PluginInfoDto {
        val service = serviceMap[id] ?: throw PluginNotSupportedException("${id}不存在")
        return PluginInfoDto(service.stateManager.enabled, service is KeyService, service.pluginInfo)
    }

    fun getNodeService(id: String): NodeService {
        return serviceMap[id] ?: throw PluginNotSupportedException("${id}不存在")
    }

    fun getKeyService(id: String): KeyService {
        val service = getNodeService(id)
        if (service is KeyService) {
            return service
        } else {
            throw PluginNotSupportedException("${id}不是KeyService")
        }
    }

    @PostConstruct
    fun construct() {
        serviceMap.values.forEach {
            it.start()
        }
    }

    @PreDestroy
    fun destroy() {
        serviceMap.values.forEach {
            it.stop()
        }
    }
}