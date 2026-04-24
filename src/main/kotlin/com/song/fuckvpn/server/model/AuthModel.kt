package com.song.fuckvpn.server.model

import com.song.fuckvpn.server.common.exception.InvalidParameterException
import com.song.fuckvpn.server.dto.UserLoginRequest
import com.song.fuckvpn.server.dto.UserUpdateConfigRequest
import com.song.fuckvpn.server.util.hash
import kotlinx.serialization.Serializable

const val salt = "d498610070174fba9e6512b63a9e2554"

@Serializable
data class UserConfigModel(val username: String, val password: String) {
    companion object {
        fun fromLoginRequest(request: UserLoginRequest) = UserConfigModel(request.username, request.password.hash(salt))

        fun fromUpdateConfigRequest(request: UserUpdateConfigRequest): UserConfigModel = UserConfigModel(
            request.username ?: throw InvalidParameterException("用户名不能为空"),
            request.password?.hash(salt) ?: throw InvalidParameterException("密码不能为空")
        )
    }

    fun fromUpdateConfigRequest(request: UserUpdateConfigRequest): UserConfigModel =
        copy(username = request.username ?: this.username, password = request.password?.hash(salt) ?: this.password)
}


data class TokenModel(val token: String)