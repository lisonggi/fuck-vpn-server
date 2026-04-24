package com.song.fuckvpn.server.controller

import com.song.fuckvpn.server.common.dto.ResultDto
import com.song.fuckvpn.server.dto.UserConfigResponse
import com.song.fuckvpn.server.dto.UserLoginRequest
import com.song.fuckvpn.server.dto.UserUpdateConfigRequest
import com.song.fuckvpn.server.model.UserConfigModel
import com.song.fuckvpn.server.service.AuthService
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")

class AuthController(val authService: AuthService) {
    @PostMapping("/login")
    fun login(@RequestBody request: UserLoginRequest, response: HttpServletResponse): ResultDto<UserConfigResponse> {
        val token = authService.login(request)
        val cookie = Cookie("token", token.token).apply {
            path = "/"
            isHttpOnly = true
            maxAge = 7 * 24 * 60 * 60 // 7天
        }
        response.addCookie(cookie)
        return ResultDto("ok", UserConfigResponse.fromLogin(request))
    }

    @DeleteMapping("/logout")
    fun logout(
        response: HttpServletResponse
    ): ResultDto<Unit> {
        authService.logout()
        val cookie = Cookie("token", "").apply {
            path = "/"          // 必须和设置时一致
            maxAge = 0          // 关键：立即删除
            isHttpOnly = true
        }
        response.addCookie(cookie)
        return ResultDto("ok")
    }

    @GetMapping("/me")
    fun me(): ResultDto<UserConfigResponse> {
        val model = authService.getUser() ?: UserConfigModel("default", "")
        return ResultDto("ok", UserConfigResponse.fromModel(model))
    }

    @PutMapping("/updateConfig")
    fun updateConfig(
        @RequestBody request: UserUpdateConfigRequest
    ): ResultDto<UserConfigResponse> {
        val model = authService.updateConfig(request)
        return ResultDto("ok", UserConfigResponse.fromModel(model))
    }
}