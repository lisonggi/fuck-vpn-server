package com.song.fuckvpn.server.store

import com.song.fuckvpn.server.util.DefaultPath
import com.song.fuckvpn.server.util.log
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class ConfigStore<T> {
    private val file: File
    private val serializer: KSerializer<T>
    private val default: () -> T

    constructor(
        path: String,
        serializer: KSerializer<T>,
        default: () -> T
    ) {
        this.file = File("${DefaultPath}/${path}")
        this.serializer = serializer
        this.default = default
    }

    private val json = Json {
        prettyPrint = true
        encodeDefaults = true
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    @Synchronized
    fun load(isCreate: Boolean = false): T {
        return try {
            json.decodeFromString(serializer, file.readText())
        } catch (e: Exception) {
            "⚠️ 配置解析失败，使用默认配置 - ${file.path}".log.info()
            val config = default()
            if (isCreate) {
                save(config)
            }
            config
        }
    }

    @Synchronized
    fun save(config: T) {
        file.parentFile?.mkdirs()

        val tempFile = File(file.absolutePath + ".tmp")

        val content = json.encodeToString(serializer, config)
        tempFile.writeText(content, Charsets.UTF_8)
        try {
            Files.move(
                tempFile.toPath(),
                file.toPath(),
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.ATOMIC_MOVE
            )
        } catch (e: Exception) {
            "⚠️ 配置保存失败: ${file.absolutePath}".log.info()
        }
    }
}