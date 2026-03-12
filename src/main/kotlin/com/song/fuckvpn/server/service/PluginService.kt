package com.song.fuckvpn.server.service

import com.song.fuckvpn.plugin.api.KeyPlugin
import com.song.fuckvpn.plugin.api.NodePlugin
import com.song.fuckvpn.plugin.api.annotation.VPNPlugin
import com.song.fuckvpn.server.common.exception.AppException
import com.song.fuckvpn.server.common.util.log
import com.song.fuckvpn.server.dto.ServiceInfo
import com.song.fuckvpn.server.enums.ServiceType
import io.github.classgraph.ClassGraph
import java.io.File
import java.net.URLClassLoader

class PluginService {
    private val serviceMap: Map<String, NodeService>

    constructor() {
        val plugins = mutableMapOf<String, NodeService>()
        val pluginDir = File("plugins")
        pluginDir.absolutePath.log.info()
        val urls = pluginDir.listFiles { f -> f.extension == "jar" }?.map { it.toURI().toURL() }?.toTypedArray()
            ?: emptyArray()
        val classLoader = URLClassLoader(urls, this::class.java.classLoader)

        ClassGraph().enableClassInfo().enableAnnotationInfo().overrideClassLoaders(classLoader).scan().use {
            val classes = it.getClassesWithAnnotation(VPNPlugin::class.java)
            classes.forEach { classInfo ->
                val clazz = classInfo.loadClass()
                val annotation = clazz.getAnnotation(VPNPlugin::class.java)
                val instance = clazz.getDeclaredConstructor().newInstance()
                val id = annotation.id
                val name = annotation.name
                var type: ServiceType
                if (KeyPlugin::class.java.isAssignableFrom(clazz)) {
                    type = ServiceType.KEY
                    val keyPlugin: KeyPlugin = instance as KeyPlugin
                    plugins[id] = KeyService(ServiceInfo(id, name, type), keyPlugin)
                    "plugin: id:${id} name:${name} type:${type}".log.info()
                } else if (NodePlugin::class.java.isAssignableFrom(clazz)) {
                    type = ServiceType.NODE
                    val nodePlugin: NodePlugin = instance as NodePlugin
                    plugins[id] = NodeService(ServiceInfo(id, name, type), nodePlugin)
                    "plugin: id:${id} name:${name} type:${type}".log.info()
                } else {
                    "Not supported plugin: id:${id} name:${name}".log.info()
                }
            }
        }
        this.serviceMap = plugins.toMap()
    }

    fun getServicesInfo(): List<ServiceInfo> {
        val serviceInfoList = mutableListOf<ServiceInfo>()
        serviceMap.forEach { (id) ->
            serviceInfoList.add(getServiceInfo(id))
        }
        return serviceInfoList
    }

    fun getServiceInfo(id: String): ServiceInfo {
        val service = serviceMap[id] ?: throw AppException("${id}不存在")
        return service.getServiceInfo()
    }

    fun getService(id: String): NodeService {
        return serviceMap[id] ?: throw AppException("${id}不存在")
    }

    fun getKeyService(id: String): KeyService {
        val service = getService(id)
        if (service is KeyService) {
            return service
        } else {
            throw AppException("${id}不是KeyService")
        }
    }
}