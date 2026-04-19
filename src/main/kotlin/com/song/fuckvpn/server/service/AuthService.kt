package com.song.fuckvpn.server.service

import com.song.fuckvpn.server.common.exception.AuthException
import com.song.fuckvpn.server.model.UserConfig
import com.song.fuckvpn.server.store.ConfigStore
import com.song.fuckvpn.server.util.IdUtil
import jakarta.annotation.PreDestroy
import kotlinx.serialization.builtins.nullable
import org.springframework.stereotype.Service

@Service
class AuthService {
    val configStore: ConfigStore<UserConfig?> = ConfigStore("UserConfig.json", UserConfig.serializer().nullable) { null }
    var userConfig: UserConfig? = configStore.load()
    var token = generateToken()

    fun login(userConfig: UserConfig): String {
        if (this.userConfig == null || this.userConfig == userConfig) {
            val token = generateToken()
            this.token = token
            return token
        }
        throw AuthException("用户名或密码错误")
    }

    fun logout() {
        this.token = generateToken()
    }

    fun checkToken(token: String): Boolean {
        return this.token == token
    }

    fun updateConfig(userConfig: UserConfig): UserConfig {
        this.userConfig = userConfig
        return userConfig
    }

    private fun generateToken(): String {
        return IdUtil.generateId().replace("-", "")
    }

    @PreDestroy
    fun stop() {
        configStore.save(userConfig)
    }
}