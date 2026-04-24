package com.song.fuckvpn.server.dto

import com.song.fuckvpn.server.model.UserConfigModel



data class UserLoginRequest(val username: String, val password: String)

data class UserUpdateConfigRequest(
    val username: String? = null,
    val password: String? = null,
)

data class UserConfigResponse(val username: String) {
    companion object {
        fun fromModel(model: UserConfigModel): UserConfigResponse = UserConfigResponse(model.username)
        fun fromLogin(request:UserLoginRequest): UserConfigResponse = UserConfigResponse(request.username)
    }
}