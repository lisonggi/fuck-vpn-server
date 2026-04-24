package com.song.fuckvpn.server.service

import com.song.fuckvpn.server.common.exception.AuthException
import com.song.fuckvpn.server.dto.UserLoginRequest
import com.song.fuckvpn.server.dto.UserUpdateConfigRequest
import com.song.fuckvpn.server.model.UserTokenModel
import com.song.fuckvpn.server.model.UserConfigModel
import com.song.fuckvpn.server.store.ConfigStore
import com.song.fuckvpn.server.util.IdUtil
import jakarta.annotation.PreDestroy
import kotlinx.serialization.builtins.nullable
import org.springframework.stereotype.Service

@Service
class AuthService {
    private val configStore: ConfigStore<UserConfigModel?> =
        ConfigStore("UserConfig.json", UserConfigModel.serializer().nullable) { null }
    private var config: UserConfigModel? = configStore.load()

    private val defaultTokenModel = UserTokenModel(generateToken())
    private var tokenModel: UserTokenModel = defaultTokenModel

    fun login(request: UserLoginRequest): UserTokenModel {
        this.config?.let {
            if (config != UserConfigModel.fromLoginRequest(request)) {
                throw AuthException("用户名或密码错误")
            }
        }
        val token = UserTokenModel(generateToken())
        this.tokenModel = token
        return token
    }

    fun logout(): UserTokenModel {
        val token = tokenModel
        this.tokenModel = defaultTokenModel
        return token
    }

    fun checkToken(token: String): Boolean {
        return this.tokenModel.token == token
    }

    fun updateConfig(request: UserUpdateConfigRequest): UserConfigModel {
        val config = this.config?.fromUpdateConfigRequest(request) ?: UserConfigModel.fromUpdateConfigRequest(request)
        this.config = config
        return config
    }

    private fun generateToken(): String {
        return IdUtil.generateId().replace("-", "")
    }

    fun getUser(): UserConfigModel? {
        return config
    }

    @PreDestroy
    fun stop() {
        configStore.save(config)
    }
}