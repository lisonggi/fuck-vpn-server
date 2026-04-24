package com.song.fuckvpn.server.service

import com.song.fuckvpn.plugin.api.annotation.VPNPlugin
import com.song.fuckvpn.plugin.api.spec.KeyPluginSpec
import com.song.fuckvpn.plugin.api.spec.NodePluginSpec
import com.song.fuckvpn.server.common.exception.PluginNotSupportedException
import com.song.fuckvpn.server.enums.ServiceType
import com.song.fuckvpn.server.model.toModel
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
            if (NodePluginSpec::class.java.isAssignableFrom(clazz)) {
                val nodePlugin: NodePluginSpec = instance as NodePluginSpec
                val pluginInfo = nodePlugin.getPluginInfo()
                val service: NodeService = if (nodePlugin is KeyPluginSpec) {
                    KeyService(pluginInfo.toModel(ServiceType.KEY), nodePlugin)
                } else {
                    NodeService(pluginInfo.toModel(ServiceType.NODE), nodePlugin)
                }
                val info = service.pluginManager().getInfo()
                plugins[info.id] = service
                "已装载 ${info}".log.info()
            }
        }
        this.serviceMap = plugins
    }

    fun getAllService(): List<NodeService> {
        return serviceMap.values.toList()
    }

    fun getService(id: String): NodeService {
        return serviceMap[id] ?: throw PluginNotSupportedException("${id}不存在")
    }

    fun getNodeService(id: String): NodeService {
        return getService(id)
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